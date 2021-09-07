package com.in28minutes.jpa.hibernate.demo.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.in28minutes.jpa.hibernate.demo.DemoApplication;
import com.in28minutes.jpa.hibernate.demo.entity.Course;
import com.in28minutes.jpa.hibernate.demo.entity.Review;
import com.in28minutes.jpa.hibernate.demo.entity.Student;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class CourseRepositoryTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CourseRepository repository;

	@Autowired
	EntityManager em;

	@Test
	public void findById_basic() {
		Course course = repository.findById(10001L);
		assertEquals("JPA in 50 Steps", course.getName());
	}

	@Test
	//@Transactional
	/*
	* One is first level cash comes into picture within the boundary of a transaction.
	If you are retrieving the same data again within the same transaction, you would not go to the database,you directly retrieve it from the first level cache.
	*
	* If you want to make the best use of first level cache, then the boundary of the transaction should start with this service method.
	So starting with your service method, all the calls to the data layer should be within the scope of the single transaction.
	Then the first level cache will be really really efficient
	*
	*  the most important thing with respect to first level cache is the boundary of the transaction.
	The ideal place to start and end the transaction is your service methods that basically your business service method.
	* */
	public void findById_firstLevelCacheDemo() {

		/*
		* details that are retrieved by the first call are not being cached, are not being used by this second call.
		This is because these are running within two different transactions because we have not specified the transaction boundary in here.
		* */

		Course course = repository.findById(10001L);
		logger.info("First Course Retrieved {}", course);

		Course course1 = repository.findById(10001L);
		logger.info("First Course Retrieved again {}", course1);

		assertEquals("JPA in 50 Steps", course.getName());

		assertEquals("JPA in 50 Steps", course1.getName());
	}


	@Test
	@DirtiesContext
	public void deleteById_basic() {
		repository.deleteById(10002L);
		assertNull(repository.findById(10002L));
	}

	@Test
	@DirtiesContext
	public void save_basic() {
		// get a course
		Course course = repository.findById(10001L);
		assertEquals("JPA in 50 Steps", course.getName());

		// update details
		course.setName("JPA in 50 Steps - Updated");
		repository.save(course);

		// check the value
		Course course1 = repository.findById(10001L);
		assertEquals("JPA in 50 Steps - Updated", course1.getName());
	}

	@Test
	@DirtiesContext
	public void playWithEntityManager() {
		repository.playWithEntityManager();
	}

	@Test
	@Transactional
	public void retrieveReviewsForCourse() {
		Course course = repository.findById(10001L);
		logger.info("{}", course.getReviews());
	}

	@Test
	@Transactional
	public void retrieveCourseForReview() {
		Review review = em.find(Review.class, 50001L);
		logger.info("{}", review.getCourse());
	}

	@Test
	@Transactional
	@DirtiesContext
	public void performance() {
		//for (int i = 0; i < 20; i++)
			//em.persist(new Course("Something" + i));
		//em.flush();

		//EntityGraph graph = em.getEntityGraph("graph.CourseAndStudents");

		EntityGraph<Course> graph = em.createEntityGraph(Course.class);
	    Subgraph<List<Student>> bookSubGraph = graph.addSubgraph("students");

	    List<Course> courses = em.createQuery("Select c from Course c", Course.class)
	        .setHint("javax.persistence.loadgraph", graph)
	        .getResultList();
	    for (Course course : courses) {
	      System.out.println(course + " " + course.getStudents());
	    }
	}

	@Test
	@Transactional
	@DirtiesContext
	public void performance_without_hint() {
	    List<Course> courses = em.createQuery("Select c from Course c", Course.class)
	        //.setHint("javax.persistence.loadgraph", graph)
	        .getResultList();
	    for (Course course : courses) {
	      System.out.println(course + " " + course.getStudents());
	    }
	}


	//@Transactional(level)
	/*
	* there are two types of Transaction annotations
	*JPA(javax) is able to manage transactions over a single database
	*Spring is able to manage transactions across multiple databases
	* */

}
