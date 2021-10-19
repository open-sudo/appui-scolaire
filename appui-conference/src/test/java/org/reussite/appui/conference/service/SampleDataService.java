package org.reussite.appui.conference.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.codec.digest.DigestUtils;
import org.reussite.appui.support.dashboard.model.CourseEntity;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.model.SubjectEntity;
import org.reussite.appui.support.dashboard.model.TeacherAvailabilityEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class SampleDataService {

	protected static final String[] SUBJECTS = { "Python", "Scratch", "Java", "Linux" , "Maths", "English", "Francais"};

	protected String[] phones = { "416", "647", "289" };

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	ZonedDateTime datesObjects[];

	public ZonedDateTime[] getDatesObjects() {
		return datesObjects;
	}

	{
		ZonedDateTime date1 = TimeUtils.getCurrentTime().plusDays(10);
		ZonedDateTime date2 = date1.plusDays(5);
		ZonedDateTime date3 = date2.plusDays(5);
		ZonedDateTime date4 = date3.plusDays(5);
		ZonedDateTime date5 = date4.plusDays(5);
		datesObjects = new ZonedDateTime[] { date1, date2, date3, date4, date5 };
	}

	public String[] getPhones() {
		return phones;
	}

	public void setPhones(String[] phones) {
		this.phones = phones;
	}

	public String getDate(int i) {
		return TimeUtils.toLongString(datesObjects[i]);
	}
	public void setDates(ZonedDateTime[] dates) {
		this.datesObjects = dates;
	}

	protected String[] surnames = new String[] { "Cooper", "Doe", "Pogba", "Zidane", "Mueller", "Kahn", "Alaba",
			"Talom", "Kengne", "Tedom", "Talla", "Howell","Nguyen","Stanley","Burton","Little","Smith" ,"Martin","Adam","Martinez"};
	protected String[] firstnames = new String[] { "Jeanne", "Jane", "David", "John", "Marie", "Celine", "Suzanne",
			"Nadine", "Sabine", "Lilianne", "Jennifer","Andrea", "Adonie", "Adian", "Justin", "Jason", "Brandon", "Sarah", "Stephanie", "Jonathan","Bryan","Blandine" , "Thomas","Michael"};

	private String videoServerUrl="https://bbb1.appui.io/bigbluebutton/api";

	private String videoServerAuthKey="GDGDGDG";

	public void generateSampleDataSync(int students, int teachers) {
		createSchedules();
		createStudentBookingEntityList(students);
		createTeacherList(teachers);
	}
	private String encode(String t) {
		try {
			return URLEncoder.encode(t,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
		}
		return t;
	}
	public void createTeacherList(int teacherListSize) {
		TeacherAvailabilityEntity.deleteAll();
		TeacherProfileEntity.deleteAll();
		List<TeacherProfileEntity> profiles = new ArrayList<TeacherProfileEntity>();
		List<TeacherAvailabilityEntity> teachers = new ArrayList<>(teacherListSize);
		for (int i = 0; i < teacherListSize; i++) {
			TeacherAvailabilityEntity teacher = generateTeacherAvailabilityEntity(i);
			teachers.add(teacher);
			profiles.add(teacher.teacherProfile);
		}
		TeacherProfileEntity.persist(profiles);
		TeacherAvailabilityEntity.persist(teachers);
		logger.info("Completed creating {} teachers", teacherListSize);
	}
	
	protected String createJoinConferenceUrl(TeacherAvailabilityEntity availability) {
		String firstName=availability.teacherProfile.firstName;
		String lastName=availability.teacherProfile.lastName;
		String meetingId=availability.conferenceId;
		String base="fullName="+encode(firstName)+"+"+encode(lastName+"'s Classroom")+"&";
		base=base+"joinViaHtml5=true&meetingID="+meetingId;
		base=base+"&password="+availability.teacherPassword;
		String checksum=DigestUtils.sha1Hex("join"+base+videoServerAuthKey);
		String baseUrl=videoServerUrl+"/join?"+base+"&checksum="+checksum;
		logger.info("Join URL computed:{}",baseUrl);
		return baseUrl;
	}
	
	public TeacherAvailabilityEntity generateTeacherAvailabilityEntity(int count) {
		
		TeacherProfileEntity profile = new TeacherProfileEntity();
		profile.id=UUID.randomUUID().toString();
		profile.phoneNumber = (phones[count % phones.length]);
		for (int i = 2; i < 9; i++) {
			profile.phoneNumber = (profile.phoneNumber + (count % i));
		}
		profile.firstName = (firstnames[(firstnames.length - ((count+1) %firstnames.length))%firstnames.length]);
		profile.lastName = (surnames[((surnames.length - 20))]);
		profile.email = ("p." + profile.firstName + "." + profile.lastName + "@aproresco.com").toLowerCase();
		profile.conferenceUrl = ((profile.firstName + "-" + profile.lastName).toLowerCase());
		List<SubjectEntity> subjects = SubjectEntity
				.find(" lower(name) = ?1", SUBJECTS[(count) % (SUBJECTS.length)].toLowerCase()).list();

		profile.subjects.addAll(subjects);
		int numberOfLevels = (count % 4) + 2;
		Set<Integer> grades = new HashSet<Integer>();

		for (int i = 0; i < numberOfLevels; i++) {
			grades.add(((count + i) % 12));
		}
		profile.grades = (grades);
		profile.persistAndFlush();

		TeacherAvailabilityEntity availability = new TeacherAvailabilityEntity();
		availability.id=UUID.randomUUID().toString();
		availability.teacherProfile = (profile);
		List<ScheduleEntity> schedules = ScheduleEntity.findAll().list();
		availability.schedule = schedules.get(count % schedules.size());
		availability.effectiveStartDate = TimeUtils.getCurrentTime();
		availability.conferenceUrl=createJoinConferenceUrl(availability);
		availability.persistAndFlush();
		return availability;
	}

	public void createSchedules() {
		for (String name : SUBJECTS) {
			for (String language : languages) {
				SubjectEntity s = new SubjectEntity();
				s.language = language;
				s.name = name;
				if(name.toLowerCase().contains("math")) {
					s.general=true;
				}
				s.id=UUID.randomUUID().toString();
				s.persistAndFlush();
				CourseEntity course1 = new CourseEntity();
				course1.grades.add(1);
				course1.grades.add(2);
				course1.subject = s;
				course1.id=UUID.randomUUID().toString();
				course1.name = name + "-" + language + "-Grade-1";
				course1.persist();

				CourseEntity course2 = new CourseEntity();
				course2.grades.add(7);
				course2.grades.add(8);
				course2.subject = s;
				course2.id=UUID.randomUUID().toString();;
				course2.name = name + "-" + language + "-Grade-7";
				course2.persist();

				CourseEntity course3 = new CourseEntity();
				course3.grades.add(5);
				course3.grades.add(6);
				course3.subject = s;
				course3.id=UUID.randomUUID().toString();
				course3.name = name + "-" + language + "-Grade-5";
				course3.persist();

				for (ZonedDateTime date : datesObjects) {
					ScheduleEntity schedule = new ScheduleEntity();
					schedule.course = course1;
					schedule.startDate = date;
					schedule.id=UUID.randomUUID().toString();
					schedule.persistAndFlush();

					schedule = new ScheduleEntity();
					schedule.id=UUID.randomUUID().toString();
					schedule.course = course2;
					schedule.startDate = date;
					schedule.persistAndFlush();

					schedule = new ScheduleEntity();
					schedule.id=UUID.randomUUID().toString();
					schedule.course = course3;
					schedule.startDate = date;
					schedule.persistAndFlush();
				}
			}
		}
	}

	String languages[] = new String[] { "FR", "EN" };

	public void createStudentBookingEntityList(int studentListSize) {
		System.out.println("Creating "+studentListSize+" bookings");
		List<StudentBookingEntity> studentList = new ArrayList<>(studentListSize);
		for (int i = 0; i < studentListSize; i++) {
			StudentBookingEntity student = generateStudentBookingEntity(i);
			studentList.add(student);
		}
		List<StudentProfileEntity> profiles = new ArrayList<StudentProfileEntity>();
		for (StudentBookingEntity b : studentList) {
			profiles.add(b.studentProfile);
		}
		StudentProfileEntity.persist(profiles);
		StudentBookingEntity.deleteAll();
		logger.info("Completed creating {} students", studentListSize);
		StudentBookingEntity.persist(studentList);
	}

	public StudentBookingEntity generateStudentBookingEntity(int count) {
		StudentBookingEntity booking = new StudentBookingEntity();
		booking.id=UUID.randomUUID().toString();
		StudentProfileEntity profile = new StudentProfileEntity();
		profile.id=UUID.randomUUID().toString();
		List<ScheduleEntity> schedules = ScheduleEntity.findAll().list();
		profile.lastName = (surnames[(count)%surnames.length]);
		booking.studentProfile = (profile);
		profile.grade = ((count % 12)) + 1;
		booking.schedule = schedules.get(count % schedules.size());
		System.out.println("reating booking with subject:"+booking.schedule.course.subject.name);
		profile.firstName = firstnames[(count)%firstnames.length];

		profile.conferenceUrl = ((profile.firstName + "-" + profile.lastName).toLowerCase());
		profile.email = ("e." + profile.firstName + "." + profile.lastName + "@aproresco.org").toLowerCase();
		return booking;
	}

	
	

	
	public static StudentProfileEntity createStudentProfileEntity() {
		StudentProfileEntity profile= new StudentProfileEntity();
		profile.countryCode=(1);
		profile.email=("mon_parent@gmail.com");
		profile.phoneNumber=("5454545454");
    	profile.firstName=("Paul");
    	profile.lastName=("Maron");
    	profile.grade=(6);
    	return profile;
	}

	public TeacherProfileEntity createTeacherProfileEntity() {
		TeacherProfileEntity profile= new TeacherProfileEntity();
    	profile.firstName=("Pauline");
    	profile.lastName=("Maroniquet");
    	profile.countryCode=(1);
    	profile.phoneNumber=("5454545454");
    	return profile;
	}
	public void generateSampleDataWithStaggeredDates() {
		long thenMinutes=58;
		 ZonedDateTime date1= ZonedDateTime.now().minusMinutes(thenMinutes);
		 ZonedDateTime date2=date1.plusMinutes(40);
		 ZonedDateTime date3=date2.plusMinutes(40);
			
		setDates(new ZonedDateTime[] {date1,date2,date3});
		createSchedules();
		createStudentBookingEntityList(250);
		createTeacherList(30);
	}
	
	public void generateDataWithGivenDates(ZonedDateTime date1, ZonedDateTime date2, ZonedDateTime date3) {
    	createSchedules();
		setDates(new ZonedDateTime[] {date1,date2,date3});
		createStudentBookingEntityList(250);
		createTeacherList(20);
	}
	

	public void generateData() {
    	createSchedules();
		createStudentBookingEntityList(250);
		createTeacherList(20);
	}

	
}
