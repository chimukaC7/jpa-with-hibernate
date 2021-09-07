package com.in28minutes.jpa.hibernate.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * with SQL we query from database tables
 * with JPQL we query from entities
 * */

@Entity
//@Table(name = "Course")
@NamedQueries(value = {
        @NamedQuery(name = "query_get_all_courses",
                query = "SELECT c FROM Course c"),
        @NamedQuery(name = "query_get_all_courses_join_fetch",
                query = "SELECT c FROM Course c JOIN FETCH c.students s"),
        @NamedQuery(name = "query_get_100_Step_courses",
                query = "SELECT c FROM Course c WHERE name LIKE '%100 Steps'")
})

@Cacheable
@SQLDelete(sql = "update course set is_deleted=true where id=?")//to be called when a DELETE is executed
@Where(clause = "is_deleted = false")//retrieve rows where is_deleted is false
public class Course {

    private static Logger LOGGER = LoggerFactory.getLogger(Course.class);

    @Id//primary key
    @GeneratedValue//JPA to generate it
    private Long id;

    @Column(nullable = false)
    private String name;

    //by default it is lazy fetch
    @OneToMany(mappedBy = "course")
    private List<Review> reviews = new ArrayList<>();

    @ManyToMany(mappedBy = "courses")
    @JsonIgnore
    private List<Student> students = new ArrayList<>();

    @UpdateTimestamp
    private LocalDateTime lastUpdatedDate;

    @CreationTimestamp
    private LocalDateTime createdDate;

    // implementing something called soft delete.
    //Some delete is done by adding a column to the database to track whether it's active or not, whether
    //it's deleted or not.
    private boolean isDeleted;

    //Whenever a row of a specific entity is deleted, there is a method that gets fired
    @PreRemove
    private void preRemove() {
        LOGGER.info("Setting isDeleted to True");
        this.isDeleted = true;
    }

    protected Course() {
    }

    public Course(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        this.students.add(student);
    }

    //only have a getter for id no setter
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Course[%s]", name);
    }
}
