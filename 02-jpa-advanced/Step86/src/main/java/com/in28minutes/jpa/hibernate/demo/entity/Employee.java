package com.in28minutes.jpa.hibernate.demo.entity;

import javax.persistence.*;

@MappedSuperclass//separate tables are created for each of the subclass
//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)//all the sub classes are mapped to one table
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)//a table per concrete entity class
//@Inheritance(strategy=InheritanceType.JOINED)//a strategy in which fields that are specific to a subclass are mapped
//to a separate table than the fields that are common to the parent class and a join is performed to instantiate the subclass

//@DiscriminatorColumn(name = "EmployeeType")
public abstract class Employee {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	protected Employee() {
	}

	public Employee(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return String.format("Employee[%s]", name);
	}
}
