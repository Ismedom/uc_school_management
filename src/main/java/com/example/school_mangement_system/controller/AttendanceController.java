package com.example.school_mangement_system.controller;

import com.example.school_mangement_system.dto.AttendanceBulkRequest;
import com.example.school_mangement_system.dto.AttendanceResponse;
import com.example.school_mangement_system.entity.AttendanceStatus;
import com.example.school_mangement_system.repository.SectionRepository;
import com.example.school_mangement_system.service.AttendanceService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final SectionRepository sectionRepository;

    @GetMapping
    public String showAttendanceSelector(Model model) {
        model.addAttribute("sections", sectionRepository.findAll());
        model.addAttribute("today", LocalDate.now());
        return "attendance/index";
    }

    @GetMapping("/mark")
    public String markAttendance(
        @RequestParam Long sectionId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        Model model
    ) {
        List<AttendanceResponse> records = attendanceService.getAttendanceBySectionAndDate(sectionId, date);

        AttendanceBulkRequest bulkRequest = new AttendanceBulkRequest();
        bulkRequest.setSectionId(sectionId);
        bulkRequest.setDate(date);

        model.addAttribute("bulkRequest", bulkRequest);
        model.addAttribute("records", records);
        model.addAttribute("section", sectionRepository.findById(sectionId).orElse(null));
        model.addAttribute("date", date);
        model.addAttribute("statuses", AttendanceStatus.values());

        return "attendance/mark";
    }

    @PostMapping("/mark")
    public String saveAttendance(@ModelAttribute AttendanceBulkRequest bulkRequest) {
        attendanceService.saveBulkAttendance(bulkRequest);
        return "redirect:/attendance?success=true";
    }

    @GetMapping("/history/{studentId}")
    public String studentHistory(@PathVariable Long studentId, Model model) {
        model.addAttribute("history", attendanceService.getStudentAttendanceHistory(studentId));
        return "attendance/history";
    }
}
