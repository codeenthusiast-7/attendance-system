package com.example.attendance.controller;

import com.example.attendance.model.Attendance;
import java.util.List;

public class AttendanceRosterWrapper {

    private List<Attendance> list;

    public List<Attendance> getList() {
        return this.list;
    }

    public void setList(List<Attendance> list) {
        this.list = list;
    }
}