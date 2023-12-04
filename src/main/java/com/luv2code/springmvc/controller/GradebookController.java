package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

	@Autowired
	private Gradebook gradebook;

	@Autowired
	private StudentAndGradeService studentAndGradeService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStudents(Model m) {
		Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
		m.addAttribute("students",collegeStudents);
		return "index";
	}

	@PostMapping("/")
	public String createStudent(@ModelAttribute("student") CollegeStudent student,Model m){
		studentAndGradeService.createStudent(student.getFirstname(),student.getLastname(),student.getEmailAddress());
		Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
		m.addAttribute("students",collegeStudents);
		return "index";
	}

	@GetMapping("/delete/student/{id}")
	public String deleteStudent(@PathVariable int id , Model m){

		if(!studentAndGradeService.checkStudentIfIsNull(id)){
			return "error";
		}

		studentAndGradeService.deleteStudent(id);
		Iterable<CollegeStudent> collegeStudents = studentAndGradeService.getGradeBook();
		m.addAttribute("students",collegeStudents);
		return "index";
	}


	@GetMapping("/studentInformation/{id}")
		public String studentInformation(@PathVariable int id, Model m) {

		if(!studentAndGradeService.checkStudentIfIsNull(id)){
			return "error";
		}

		studentAndGradeService.configureStudentInformationModel(id,m);

		return "studentInformation";
		}


		@PostMapping(value = "/grades")
		public String createGrade(@RequestParam("grade")double grade,
								  @RequestParam("gradeType")String gradeType,
								  @RequestParam("studentId")int studentId,
								  Model m){

		if(!studentAndGradeService.checkStudentIfIsNull(studentId)){
			return "error";
		}

		boolean success = studentAndGradeService.createGrade(grade,studentId,gradeType);

		if (!success){
			return "error";
		}

		studentAndGradeService.configureStudentInformationModel(studentId,m);

		return "studentInformation";
		}

		@GetMapping("/grades/{id}/{gradeType}")
	    public String deleteGrade(@PathVariable int id,@PathVariable String gradeType,Model m){

		int studentId = studentAndGradeService.deleteGrade(id,gradeType);

		if(studentId == 0){
			return "error";
		}

		studentAndGradeService.configureStudentInformationModel(studentId,m);
		return "studentInformation";
		}
}
