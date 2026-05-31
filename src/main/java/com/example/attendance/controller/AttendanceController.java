package com.example.attendance.controller;

import com.example.attendance.model.Attendance;
import com.example.attendance.model.Subject;
import com.example.attendance.model.Student;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.SubjectService;
import com.example.attendance.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final SubjectService subjectService;
    private final StudentService studentService;

    public AttendanceController(AttendanceService attendanceService, SubjectService subjectService, StudentService studentService) {
        this.attendanceService = attendanceService;
        this.subjectService = subjectService;
        this.studentService = studentService;
    }

    // 1. Show the Select Course & Date page
    @GetMapping
    public String showAttendanceHub(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("currentDate", LocalDate.now());
        return "attendance/hub"; // Maps to templates/attendance/hub.html
    }

    // 2. Load the Student Roster Sheet for the chosen subject and date (With Dynamic Syncing)
    @GetMapping("/take")
    public String loadRosterSheet(@RequestParam("subjectId") Long subjectId,
                                  @RequestParam("date") String dateStr, Model model) {

        LocalDate date = LocalDate.parse(dateStr);
        Subject subject = subjectService.getSubjectById(subjectId);
        List<Student> allStudents = studentService.getAllStudents();

        List<Attendance> existingRecords = attendanceService.getAttendanceBySubjectAndDate(subjectId, date);
        List<Attendance> rosterSheet = new ArrayList<>();

        if (!existingRecords.isEmpty()) {
            rosterSheet.addAll(existingRecords);

            List<Long> existingStudentIds = existingRecords.stream()
                    .map(record -> record.getStudent().getId())
                    .collect(Collectors.toList());

            for (Student student : allStudents) {
                boolean studentAlreadyExists = existingStudentIds.contains(student.getId());

                if (!studentAlreadyExists) {
                    Attendance newRecord = new Attendance();
                    newRecord.setStudent(student);
                    newRecord.setSubject(subject);
                    newRecord.setDate(date);
                    newRecord.setStatus("Present");
                    rosterSheet.add(newRecord);
                }
            }
        } else {
            for (Student student : allStudents) {
                Attendance record = new Attendance();
                record.setStudent(student);
                record.setSubject(subject);
                record.setDate(date);
                record.setStatus("Present");
                rosterSheet.add(record);
            }
        }

        AttendanceRosterWrapper wrapperObject = new AttendanceRosterWrapper();
        wrapperObject.setList(rosterSheet);

        model.addAttribute("rosterWrapper", wrapperObject);
        model.addAttribute("subject", subject);
        model.addAttribute("date", date);

        return "attendance/take";
    }

    // 3. Save or Update the marked attendance spreadsheet
    @PostMapping("/save")
    public String saveAttendanceSheet(@ModelAttribute("rosterWrapper") AttendanceRosterWrapper wrapper) {
        if (wrapper != null && wrapper.getList() != null && !wrapper.getList().isEmpty()) {
            attendanceService.saveAttendanceRecords(wrapper.getList());
        }
        return "redirect:/admin/attendance?success=true";
    }

    // 4. Show the Report Stream Selection form layout
    @GetMapping("/report-selector")
    public String showReportSelector(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        return "attendance/report-selector";
    }

    // 5. Compile and calculate percentages for the dashboard metric table
    @GetMapping("/generate-report")
    public String generateAttendanceReport(@RequestParam("subjectId") Long subjectId, Model model) {
        Subject subject = subjectService.getSubjectById(subjectId);
        List<Student> allStudents = studentService.getAllStudents();

        List<AttendanceReportRow> reportRows = new ArrayList<>();

        List<Attendance> allSubjectLogs = attendanceService.getAllAttendanceRecords().stream()
                .filter(log -> log.getSubject().getId().equals(subjectId))
                .collect(Collectors.toList());

        long totalSessions = allSubjectLogs.stream()
                .map(Attendance::getDate)
                .distinct()
                .count();

        for (Student student : allStudents) {
            long present = 0;
            long absent = 0;

            for (Attendance log : allSubjectLogs) {
                if (log.getStudent().getId().equals(student.getId())) {
                    if ("Present".equalsIgnoreCase(log.getStatus())) {
                        present++;
                    } else if ("Absent".equalsIgnoreCase(log.getStatus())) {
                        absent++;
                    }
                }
            }

            double percent = 0.0;
            if (totalSessions > 0) {
                percent = ((double) present / totalSessions) * 100.0;
            } else {
                percent = 100.0;
            }

            AttendanceReportRow row = new AttendanceReportRow(
                    student.getId(),
                    student.getName(),
                    student.getKid(),
                    present,
                    absent,
                    percent
            );
            reportRows.add(row);
        }

        model.addAttribute("subject", subject);
        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("reportRows", reportRows);

        return "attendance/report";
    }

    // 6. Render the specialized live camera tracker capture layout interface pane
    @GetMapping("/scanner")
    public String openLiveScannerConsole(@RequestParam("subjectId") Long subjectId,
                                         @RequestParam("date") String dateStr, Model model) {
        model.addAttribute("subject", subjectService.getSubjectById(subjectId));
        model.addAttribute("date", LocalDate.parse(dateStr));
        return "attendance/scanner"; // Maps cleanly to templates/attendance/scanner.html
    }

    // 7. REST API Endpoint to process rapid incoming browser QR camera scans asynchronously
    @ResponseBody
    @PostMapping("/scan-checkin")
    public ResponseEntity<String> processQrCheckIn(
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("date") String dateStr,
            @RequestParam("kid") String kid) {

        try {
            LocalDate date = LocalDate.parse(dateStr);

            // Fetch existing logs for this date and subject
            List<Attendance> existingRecords = attendanceService.getAttendanceBySubjectAndDate(subjectId, date);

            // If the sheet has never been initialized via manual grid check, build it on the fly
            if (existingRecords.isEmpty()) {
                List<Student> allStudents = studentService.getAllStudents();
                Subject subject = subjectService.getSubjectById(subjectId);
                List<Attendance> freshRoster = new ArrayList<>();

                for (Student student : allStudents) {
                    Attendance record = new Attendance();
                    record.setStudent(student);
                    record.setSubject(subject);
                    record.setDate(date);
                    // Default to Absent so checking in flips them to Present dynamically!
                    record.setStatus(student.getKid().trim().equalsIgnoreCase(kid.trim()) ? "Present" : "Absent");
                    freshRoster.add(record);
                }
                attendanceService.saveAttendanceRecords(freshRoster);
                existingRecords = freshRoster;

                // Return immediate confirmation if the scanning student initialized the sheet
                for (Attendance record : existingRecords) {
                    if (record.getStudent().getKid().trim().equalsIgnoreCase(kid.trim())) {
                        return ResponseEntity.ok("Successfully marked: " + record.getStudent().getName());
                    }
                }
            }

            // Scan the active sheet matrix for a student matching the scanned KID code
            for (Attendance record : existingRecords) {
                if (record.getStudent().getKid().trim().equalsIgnoreCase(kid.trim())) {

                    // Toggle their individual status to Present and commit back to MySQL
                    record.setStatus("Present");
                    List<Attendance> updateList = new ArrayList<>();
                    updateList.add(record);
                    attendanceService.saveAttendanceRecords(updateList);

                    return ResponseEntity.ok("Successfully marked: " + record.getStudent().getName());
                }
            }

            return ResponseEntity.status(404).body("Student with Roll No " + kid + " is not registered in this directory.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server Error processing scanner transaction.");
        }
    }
}