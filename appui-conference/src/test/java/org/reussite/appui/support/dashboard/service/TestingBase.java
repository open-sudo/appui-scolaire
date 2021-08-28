package org.reussite.appui.support.dashboard.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.TransactionManager;

import org.reussite.appui.support.dashbaord.utils.TimeUtils;
import org.reussite.appui.support.dashboard.model.Schedule;
import org.reussite.appui.support.dashboard.model.StudentBooking;
import org.reussite.appui.support.dashboard.model.StudentParent;
import org.reussite.appui.support.dashboard.model.StudentProfile;
import org.reussite.appui.support.dashboard.model.TeacherProfile;
import org.reussite.appui.support.dashboard.model.TeacherTenant;
import org.reussite.appui.support.dashboard.model.TenantProfile;

public class TestingBase {
	@Inject
	protected TransactionManager tm;
	@Inject
	protected SampleDataService sampleDataService;

	
	public static StudentProfile createStudentProfile() {
		
    	StudentProfile profile= new StudentProfile();
    	
    	
    	StudentParent parent= new StudentParent();
       	parent.countryCode=(1);
       	parent.email=("mon_parent@gmail.com");
       	parent.phoneNumber=("5454545454");
		parent.tenants.add(new TenantProfile(SampleDataService.tenants[0]));

    	parent.persistAndFlush();

    	profile.parent=parent;
    	
    	profile.firstName=("Paul");
    	profile.lastName=("Maron");
    	profile.schoolName=("Ecole de la Marre");
    	profile.schoolBoard=("Board");
    	profile.grade=(6);
    	profile.tenants.add(new TenantProfile(SampleDataService.tenants[0]));

    	return profile;
	}
	
	
	public static StudentBooking createStudentBooking(String id) {
		StudentProfile profile=new StudentProfile();
    	profile.id=id;
    	StudentBooking booking= new StudentBooking();
    	booking.schedule=Schedule.findBySubjectAndTenantKey("maths",SampleDataService.tenants[0]).get(0);
    	booking.tenant=new TenantProfile(SampleDataService.tenants[0]);
    	booking.studentProfile=profile;
    	return booking;
	}
	
	

	public TeacherProfile createTeacherProfile() {
		TeacherProfile profile= new TeacherProfile();
    	profile.firstName=("Pauline");
    	profile.lastName=("Maroniquet");
    	profile.countryCode=(1);
    	profile.phoneNumber=("5454545454");
    	profile.schoolName=("Ecole de la Marre");
    	profile.externalEmail=("mon_parent@gmail.com");
    	profile.tenants=new HashSet<TeacherTenant>();
    	TenantProfile t= new TenantProfile();
    	t.key=SampleDataService.tenants[0];
    	TeacherTenant tt= new TeacherTenant(t);
    	profile.tenants.add(tt);
    	return profile;
	}
	public void generateSampleDataWithStaggeredDates() {
    	sampleDataService.createTenantList();
    	
		long thenMinutes=58;
		 ZonedDateTime date1= ZonedDateTime.now().minusMinutes(thenMinutes);
		 String formatedDate1=TimeUtils.toLongString(date1);
		 ZonedDateTime date2=date1.plusMinutes(40);
		 String formatedDate2=TimeUtils.toLongString(date2);
		 ZonedDateTime date3=date2.plusMinutes(40);
		 String formatedDate3=TimeUtils.toLongString(date3);
		 
			
			
		 sampleDataService.setDatesObjects(new ZonedDateTime[] {date1,date2,date3});
		sampleDataService.setDates(new String[] {formatedDate1,formatedDate2,formatedDate3});
		sampleDataService.createScheduleList();
		sampleDataService.createStudentBookingList(50);
		sampleDataService.createTeacherList(30);
	}
	
	public void generateDataWithGivenDates(ZonedDateTime date1, ZonedDateTime date2, ZonedDateTime date3) {
    	sampleDataService.createTenantList();
    	sampleDataService.createScheduleList();

		String formatedDate1=TimeUtils.toLongString(date1);
		String formatedDate2=TimeUtils.toLongString(date2);
		String formatedDate3=TimeUtils.toLongString(date3);
		sampleDataService.setDates(new String[] {formatedDate1,formatedDate2,formatedDate3});
		sampleDataService.createStudentBookingList(50);
		sampleDataService.createTeacherList(20);
	}
	
	

	public void generateData() {
    	sampleDataService.createTenantList();
    	sampleDataService.createScheduleList();

		sampleDataService.createStudentBookingList(50);
		sampleDataService.createTeacherList(20);
	}

	
}
