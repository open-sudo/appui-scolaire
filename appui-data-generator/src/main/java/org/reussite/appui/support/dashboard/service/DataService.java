package org.reussite.appui.support.dashboard.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.reussite.appui.support.dashboard.domain.Course;
import org.reussite.appui.support.dashboard.domain.MonetaryAmount;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.domain.StudentBooking;
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.domain.StudentProfile;
import org.reussite.appui.support.dashboard.domain.Tag;
import org.reussite.appui.support.dashboard.domain.TeacherAvailability;
import org.reussite.appui.support.dashboard.domain.TeacherComment;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DataService {

		private final Logger logger = LoggerFactory.getLogger(this.getClass());
		protected static final String [] SUBJECTS= {"Python","Scratch", "Java", "Linux"};
	    
	    protected String [] phones= {"416","647","289"};
	    public static String [] tenants= {"alpha","beta","gamma"};
	    protected String [] dates={"08/02/2021 09:00:00 -0500","08/02/2021 18:00:00 -0500", "08/02/2021 14:00:00 -0500",
	    		"08/09/2021 09:00:00 -0500","08/09/2021 18:00:00 -0500", "08/09/2021 14:00:00 -0500",
	    		"07/19/2021 09:00:00 -0500","07/19/2021 18:00:00 -0500", "07/19/2021 14:00:00 -0500",
	    		"07/26/2021 09:00:00 -0500","07/26/2021 18:00:00 -0500", "07/26/2021 14:00:00 -0500"};
	   

	    ZonedDateTime datesObjects[];

	    private String descriptionsEn[] ={"Python is the most popular programming language. It is incredibly easy to use and learn for new beginners. The huge community around it is very welcoming and supportive."
	    ,"Scratch is designed to be fun, educational, and easy to learn. Its block-based and visual programming concepts make it a favorit among kids. Scratch is a great tool to start programming with."
	    ,"Java is the enterprise programming language. A rich API, a robust community, and numerous application domains are key to Java pervasiveness. Java is frequent in high schol and academia courses."
	    ,   "Linux is the heart of the current digital revolution. 85% of all smartphones are based on Linux. Linux took over the web. Now, It's taking over the world: televisions, thermostats, routers, refrigerators,  cars, planes are just a few examples of Linux usage."};
	    
	    private String descriptionsFr[] ={"Python est le langage de programmation le plus populaire au monde. Il est incroyablement facile à utiliser et à apprendre pour les débutants. L'immense communauté qui l'entoure est très accueillante."
	    	    ,"Scratch est amusant, éducatif et facile à apprendre. Ses concepts de programmation qui sont visuels et basés sur des blocs en font un favori parmi les enfants. Scratch est un excellent outil pour commencer à programmer."
	    	    ,"Java est le langage de programmation pour l'entreprise. Une interface de programmation riche, une communauté solide et de nombreux domaines d'application sont la clé de l'omniprésence de Java. Java est fréquent dans les cours de niveau secondaire et universitaire."
	    	    ,    "Linux is the heart of the current digital revolution. 85% of all smartphones are based on Linux. Linux took over the web. Now, It's taking over the world: televisions, thermostats, routers, refrigerators,  cars, planes are just a few examples of Linux usage."};

	    public ZonedDateTime[] getDatesObjects() {
			return datesObjects;
		}
		public void setDatesObjects(ZonedDateTime[] datesObjects) {
			this.datesObjects = datesObjects;
		}
		public interface DateTimeFormats {	
		    String DATETIME_FORMAT = "MM/dd/yyyy HH:mm:ss Z";
		    String SIMPLE_DATETIME_FORMAT = "MM/dd/yyyy HH:mm";
		}
		{
	    	
			ZonedDateTime date1= TimeUtils.getCurrentTime().plusDays(10);
			ZonedDateTime date2= date1.plusDays(5);
			ZonedDateTime date3= date2.plusDays(5);
			ZonedDateTime date4= date3.plusDays(5);
			ZonedDateTime date5= date4.plusDays(5);

			datesObjects = new ZonedDateTime[] {date1,date2,date3,date4,date5};
			
		
			datesObjects= new ZonedDateTime[dates.length];
			//dates= new String[] {formatedDate1,formatedDate2,formatedDate3};
			for(int i=0; i<dates.length; i++) {
				datesObjects[i]=(TimeUtils.getDateFromString(dates[i]));
			}
	    }

	    Random random=new Random();
	    //   String images[]= new String [] {"Python","Java","Scratch","LinuxFundamentals"};
	    String images[]= new String [] {"Python","Scratch","Java", "Linux"};

	    Float prices[]=new Float[] {0f,10f,100f};
       	String languages[]= new String[]{"FR","EN"};
       	public String[] getPhones() {
			return phones;
       	}
		public void setPhones(String[] phones) {
			this.phones = phones;
		}
		public String[] getDates() {
			return dates;
		}
		public void setDates(String[] dates) {
			this.dates = dates;
		}

	protected List<String> surnames;
	   protected List<String> firstnames;

	@Inject
    @RestClient
    protected TagService tagService;
	@Inject
    @RestClient
    protected CourseService courseService;
	@Inject
    @RestClient
    protected ScheduleService scheduleService;
	@Inject
    @RestClient
    protected StudentProfileService studentService;
	@Inject
    @RestClient
    protected StudentParentService parentService;
	@Inject
    @RestClient
    protected TeacherCommentService commentService;
	@Inject
    @RestClient
    protected StudentBookingService bookingService;
	@Inject
    @RestClient
    protected TeacherProfileService teacherService;
	@Inject
    @RestClient
    protected TeacherAvailabilityService availabilityService;
	
	String tenantKey="alpha";
    List<StudentBooking> bookings = new ArrayList<>();
    List<TeacherAvailability> availabilities = new ArrayList<>();
    List<StudentProfile> students = new ArrayList<StudentProfile>();
    List<TeacherProfile> teachers = new ArrayList<>();
    List<StudentParent> parents = new ArrayList<>();
	   protected List<String> readLines(InputStream resource){
			 List<String> lines= new ArrayList<String>();
			 BufferedReader reader=null;
		 try {
			 reader = new BufferedReader( new InputStreamReader(resource)) ;
			 String line="";
			 do{
				 line=reader.readLine();
				 if(line!=null) {
					 lines.add(line);
				 }
			 }while(line!=null);
			 
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 if(reader!=null) {
			 try {
				reader.close();
			} catch (IOException e) {
			}
		 }
		 return lines;
	   }
   
       public void createTeacherList(int teacherListSize) {

    
       	if(surnames==null) {
       		populateNameList();
       		logger.info("Number of surnames loaded:{}",surnames.size());
       		logger.info("Number of lastnames loaded:{}",firstnames.size());
       	}
           for (int i = 0; i < teacherListSize; i++) {
              TeacherAvailability availability =generateTeacherAvailability(i);
              availabilities.add(availability);
           }
           logger.info("Completed creating {} teachers",teacherListSize);
       }

       public TeacherAvailability generateTeacherAvailability(int count) {
           
           TeacherProfile profile = new TeacherProfile();
           int numberOfSubjects=(count%4)+1;
           profile.setPhoneNumber(phones[count%phones.length]);
           for(int i=2; i<9; i++) {
        	   profile.setPhoneNumber(profile.getPhoneNumber()+(count%i));
           }
           profile.setSchoolName("School"+count);
           profile.setLastName(firstnames.get(firstnames.size()-count-10));
       	   profile.setFirstName(surnames.get((surnames.size()-count-20)));
           profile.setEmail((profile.getFirstName()+"."+profile.getLastName()+random.nextLong()+"@gmail.com").toLowerCase());
           Set<String>subjects= new  HashSet<String>();
           for(int  i=0;i<numberOfSubjects; i++) {
           	subjects.add(SUBJECTS[(i+count)%(SUBJECTS.length)]);
           }
           profile.setSubjects(subjects);
           int numberOfLevels=(count%4)+2;
           Set<Integer>grades= new  HashSet<Integer>();

           for(int  i=0;i<numberOfLevels; i++) {
           	grades.add(((count+i)%12));
           }
           profile.setGrades(grades);
           profile=teacherService.create(tenantKey, profile);
           teachers.add(profile);
           TeacherAvailability availability = new TeacherAvailability();

           availability.setEffectiveStartDate(TimeUtils.getCurrentTime());//
           availability.setTeacherProfileId(profile.getId());
           		
           availability.setScheduleId(schedules.get(count%schedules.size()).getId());
           availability.setEffectiveStartDate(TimeUtils.getCurrentTime());
           availability=availabilityService.create(tenantKey, availability);
           availabilities.add(availability);
           return availability;
       }

	   public void createStudentBookingList(int studentListSize) {
	    	if(surnames==null) {
	    		populateNameList();
	    		logger.info("Number of surnames loaded:{}",surnames.size());
	    		logger.info("Number of lastnames loaded:{}",firstnames.size());
	    	}
	        for (int i = 0; i < studentListSize; i++) {
	            StudentBooking student = generateStudentBooking(i);
	            bookings.add(student);
	        }
	        logger.info("Completed creating {} students",studentListSize);
	    }
	   public StudentBooking generateStudentBooking(int count) {
	        StudentBooking booking = new StudentBooking();
	        StudentProfile profile= new StudentProfile();
	        Random r= new Random();
	        
	        StudentParent parent= new StudentParent();
	    	parent.setFirstName(firstnames.get( count));
	    	parent.setCountryCode(2+count);
	    	parent.setPhoneNumber(phones[count%phones.length]);
	    	for(int i=2; i<9; i++) {
	    		parent.setPhoneNumber(parent.getPhoneNumber()+(count%i));
	           }
	    	parent.setLastName(surnames.get(count));
	    	parent.setLanguage(r.nextInt()%2==0?"EN":"FR");
	    	parent.setEmail((parent.getFirstName()+"."+parent.getLastName()+random.nextLong()+"@gmail.com").toLowerCase());
	    	logger.info("Creating parent:{}",parent);
	    	parent=parentService.create(tenantKey, parent);
			logger.info("Parent  created:{}",parent);
			parents.add(parent);
	    	profile.setParentId(parent.getId());
	    	profile.setFirstName(firstnames.get( count+200));
	    	profile.setLastName(surnames.get(count));
	    	profile=studentService.create(tenantKey, profile);
	    	profile.setGrade(((count%12))+1);
	    	profile.setEmail((profile.getFirstName()+"."+profile.getLastName()+random.nextLong()+"@gmail.com").toLowerCase());
	    	logger.info("Creating student:{}",profile);
	    	profile=studentService.create(tenantKey, profile);
	    	logger.info("Student created:{}",profile);
	    	students.add(profile);

	    	booking.setStudentProfileId(profile.getId());
	    	logger.info("Number of schedules:{}",schedules.size());
	        booking.setScheduleId(schedules.get(count%schedules.size()).getId());
	    	logger.info("Creating booking:{}",booking);

	        booking=bookingService.create(tenantKey, booking);
	        bookings.add(booking);
	    	logger.info("Bbooking created:{}",booking);

	    	return booking;
	    }

	  public void populateNameList() {
	
		   try (InputStream inputStream = getClass().getResourceAsStream("/firstnames.txt");
			         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				surnames= readLines(inputStream);
			    } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   try (InputStream inputStream = getClass().getResourceAsStream("/surnames.txt");
			         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				firstnames= readLines(inputStream);
	
			    } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	   }
		List<Schedule> schedules= new ArrayList<Schedule>();

	   public void createScheduleList() {
		   schedules= new ArrayList<Schedule>();
	    	for(int l=0; l<languages.length; l++) {
	    		for(int j=0; j<SUBJECTS.length; j++) {
	    			Course course= new Course();
	    	    	course.setName(SUBJECTS[j]);
	    	    	course.setSubject(SUBJECTS[j]);
	    	    	course.setId(null);
	    	    	course.setDescription(languages[l].toLowerCase().contains("en")?descriptionsEn[0]:descriptionsFr[0]);
	    	    	course.setLanguage(languages[l]);
     	    		course.setImageUrl("https://s3.ca-central-1.amazonaws.com/reussite.ca/product-images/"+(images[j])+"-introduction-EN.jpg");
     	    		course.setDurationInMinutes(60);
	    	    	course.setPrices(Arrays.asList(new MonetaryAmount(new BigDecimal(12.34567), "CAD")));
	    	    	HashSet<Integer> grades=new HashSet<Integer>();
	    	    	for(int m=0; m<4; m++) {
     	    			grades.add(m+1);
     	    		}
	    	    	Random r= new Random();
	    	    	course.setGrades(grades);
	    	    	logger.info("Creating course:{}",course);
	    	    	List<Course> courses=courseService.create(tenantKey, Arrays.asList(course));
	    			 

	    				for(int d=0; d<datesObjects.length; d++) {
	    					if(datesObjects[d].isBefore(TimeUtils.getCurrentTime().plusDays(10)) && !SUBJECTS[j].toLowerCase().contains("scratch")) {
	    						continue;
	    					}
	    					if(datesObjects[d].getHour()<12 && !SUBJECTS[j].toLowerCase().contains("linux")) {
	    						for(int i=0; i<5; i+=2) {
	    							Schedule p= new Schedule();
	    							p.setStartDate(datesObjects[d].plusDays(i));
	    	         	    		p.setEndDate(p.getStartDate().plusHours(1));
	    	         	    		p.setCourseId(courses.get(r.nextInt(courses.size())).getId());
	    	         	    		p.setRepeatPeriodInDays(3);
	    	         	    		schedules.add(p);
	    						}
	    					}
	    				}
	    		}
	    	}
	    	schedules=scheduleService.create(tenantKey, schedules);
			logger.info("Number of schedules created:{}",schedules.size());
    	}
	   
	   public void createTeacherComments() {
		   for(TeacherProfile teacher:teachers) {
			   for(StudentProfile student:students) {
				   for(StudentParent parent:parents) {
					   for(StudentBooking booking:bookings) {
						   TeacherComment comment = new TeacherComment();
					    	comment.setCommenter(teacher);
					    	comment.setStudentBooking(booking);
					    	comment.setStudentProfile(student);
					    	comment.setStudentParent(parent);
					    	comment.setContent("Example comment");
					    	commentService.create(tenantKey, comment);
					   }
				   }
			   }
		   }
	    	

	   }
	public void generateData(Integer teacherCount, Integer studentCount) {
		createScheduleList();
		createStudentBookingList(studentCount);
		createTeacherList(teacherCount);
		createTeacherComments();
		String tenantKey="alpha";
		Tag tag= new Tag("Blue"+UUID.randomUUID().toString());
		tag=tagService.create(tenantKey, tag);
		logger.info("Tag  created:{}",tag);

		StudentParent parent= new StudentParent();
    	parent.setFirstName("Peter");
    	parent.setCountryCode(1);
    	SecureRandom r= new SecureRandom();
    	parent.setPhoneNumber(r.nextLong()+"");
    	parent.setLastName("Moonsoon");
    	parent.setId(null);
    	
    	parent=parentService.create(tenantKey, parent);
		logger.info("Parent  created:{}",parent);

		StudentProfile student= new StudentProfile();
    	student.setParentId(parent.getId());
    	student.setFirstName("Jane");
    	student.setLastName("Joe "+r.nextInt());
    	student.setGrade(4);
    	student=studentService.create(tenantKey, student);
		logger.info("Student  created:{}",student);

		Course course= new Course();
    	course.setName("Quarkus Programming");
    	course.setSubject("Quarkus for CRUD");
    	course.setId(null);
    	course.setPrices(Arrays.asList(new MonetaryAmount(new BigDecimal(12.34567), "CAD")));
    	logger.info("Creating course:{}",course);
    	List<Course> courses=courseService.create(tenantKey, Arrays.asList(course));
		logger.info("Course  created:{}",courses.get(0));

		Schedule schedule= new Schedule();
    	schedule.setCourseId(courses.get(0).getId());
    	schedule.setStartDate(TimeUtils.getCurrentTime());
    	schedule.setEndDate(TimeUtils.getCurrentTime());
    	
    	List<Schedule>schedules=scheduleService.create(tenantKey, Arrays.asList(schedule));
    	
    	logger.info("Schedule created:{}",schedules.get(0));
    	
    	TeacherProfile teacher= new TeacherProfile();
    	teacher.setCreateDate(null);
    	teacher.setFirstName("Jane"+UUID.randomUUID().toString());
    	teacher.setLastName("Joe");
    	teacher.setEmail(UUID.randomUUID().toString()+".it@let.do");
    	teacher=teacherService.create(tenantKey, teacher);
    	logger.info("Teacher created:{}",teacher);

    	
    	StudentBooking booking= new StudentBooking();
    	booking.setStudentProfileId(student.getId());
    	booking.setCreateDate(null);
    	booking.setScheduleId(schedules.get(0).getId());
    	
    	logger.info("Creating booking:{}",booking);
    	booking=bookingService.create(tenantKey, booking);
    	
    	logger.info("Booking created:{}",booking);

    	TeacherAvailability availability= new TeacherAvailability();
    	availability.setTeacherProfileId(teacher.getId());
    	availability.setCreateDate(null);
    	availability.setScheduleId(schedules.get(0).getId());
    	availability.setTeacherProfileId(teacher.getId());
    	
    	logger.info("Creating availability:{}",availability);
    	availability=availabilityService.create(tenantKey, availability);
    	logger.info("Availability created:{}",availability);

	}



}
