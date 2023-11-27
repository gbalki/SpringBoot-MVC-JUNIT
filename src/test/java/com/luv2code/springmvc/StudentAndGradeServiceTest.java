package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @BeforeEach
    public void setUpDatabase(){
        jdbc.execute("insert into student(id,firstName,lastName,email_address)" + "values (1,'Serhat','Balkı','serhatbalki97@gmail.com')");
    }


    @Test
    public void createStudentService(){
        studentService.createStudent("Serhat","Balkı","serhatbalki97@gmail.com");

        CollegeStudent student = studentDao.findByEmailAddress("serhatbalki97@gmail.com");

        assertEquals("serhatbalki97@gmail.com",student.getEmailAddress(),"find by email");
    }

    @Test
    public void isStudentNullCheck(){
        assertTrue(studentService.checkStudentIfIsNull(1));
        assertFalse(studentService.checkStudentIfIsNull(0));
    }

    @Test
    public void deleteStudentService(){
      Optional<CollegeStudent> deleteStudentCollegeStudent = studentDao.findById(1);

      assertTrue(deleteStudentCollegeStudent.isPresent(),"Return True");

      studentService.deleteStudent(1);

      deleteStudentCollegeStudent = studentDao.findById(1);

      assertFalse(deleteStudentCollegeStudent.isPresent(),"Return false");

    }

    @Sql("/insertData.sql")
    @Test
    public void getGradeBookService(){
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradeBook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent:iterableCollegeStudents){
            collegeStudents.add(collegeStudent);
        }

        assertEquals(4,collegeStudents.size());
    }

    @AfterEach
    public void cleanDatabase(){
        jdbc.execute("DELETE FROM student");
    }

}
