package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @BeforeEach
    public void setUpDatabase(){
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddHistoryGrade);
        jdbc.execute(sqlAddScienceGrade);
    }


    @Test
    public void createStudentService(){
        studentService.createStudent("Ferhat","Balkı","ferhatbalki97@gmail.com");

        CollegeStudent student = studentDao.findByEmailAddress("ferhatbalki97@gmail.com");

        assertEquals("ferhatbalki97@gmail.com",student.getEmailAddress(),"find by email");
    }

    @Test
    public void isStudentNullCheck(){
        assertTrue(studentService.checkStudentIfIsNull(1));
        assertFalse(studentService.checkStudentIfIsNull(0));
    }

    @Test
    public void deleteStudentService(){
      Optional<CollegeStudent> deletedStudentCollegeStudent = studentDao.findById(1);
      Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);
      Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);
      Optional<MathGrade> deletedMathGrade = mathGradeDao.findById(1);

      assertTrue(deletedStudentCollegeStudent.isPresent(),"Return True");
      assertTrue(deletedHistoryGrade.isPresent());
      assertTrue(deletedMathGrade.isPresent());
      assertTrue(deletedScienceGrade.isPresent());

      studentService.deleteStudent(1);

      deletedStudentCollegeStudent = studentDao.findById(1);
      deletedMathGrade = mathGradeDao.findById(1);
      deletedScienceGrade = scienceGradeDao.findById(1);
      deletedHistoryGrade = historyGradeDao.findById(1);

      assertFalse(deletedStudentCollegeStudent.isPresent(),"Return false");
      assertFalse(deletedMathGrade.isPresent());
      assertFalse(deletedScienceGrade.isPresent());
      assertFalse(deletedHistoryGrade.isPresent());

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

    @Test
    public void createGradeService(){

        assertTrue(studentService.createGrade(80.50,1,"math"));
        assertTrue(studentService.createGrade(80.50,1,"science"));
        assertTrue(studentService.createGrade(80.50,1,"history"));

        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        assertTrue(((Collection<MathGrade>)mathGrades).size() == 2,"Student has math grades");
        assertTrue(((Collection<ScienceGrade>)scienceGrades).size() == 2,"Student has science grades");
        assertTrue(((Collection<HistoryGrade>)historyGrades).size() == 2,"Student has history grades");
    }

    @Test
    public void createGradeServiceFalse(){

        assertFalse(studentService.createGrade(105,1,"math"));
        assertFalse(studentService.createGrade(-5,1,"math"));
        assertFalse(studentService.createGrade(80.50,2,"math"));
        assertFalse(studentService.createGrade(80.50,1,"literature"));
    }

    @Test
    public void deleteGradeService(){

        assertEquals(1,studentService.deleteGrade(1,"math"),"Return student id after delete");
        assertEquals(1, studentService.deleteGrade(1,"science"));
        assertEquals(1, studentService.deleteGrade(1,"history"));
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOfZero(){

        assertEquals(0,studentService.deleteGrade(0,"math"));
        assertEquals(0,studentService.deleteGrade(1,"literature"));
    }

    @Test
    public void studentInformation(){

        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1,gradebookCollegeStudent.getId());
        assertEquals("Serhat",gradebookCollegeStudent.getFirstname());
        assertEquals("Balki",gradebookCollegeStudent.getLastname());
        assertEquals("serhatbalki97@gmail.com",gradebookCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() ==1);
    }

    @Test
    public void studentInformationNull(){

        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);

        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    public void cleanDatabase(){
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
        jdbc.execute(sqlDeleteScienceGrade);
    }

}
