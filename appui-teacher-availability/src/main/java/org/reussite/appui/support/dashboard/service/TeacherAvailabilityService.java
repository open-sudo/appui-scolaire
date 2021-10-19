package org.reussite.appui.support.dashboard.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.reussite.appui.support.dashbaord.exceptions.AvailabilityOverlapException;
import org.reussite.appui.support.dashbaord.mapper.ScheduleMapper;
import org.reussite.appui.support.dashbaord.mapper.StudentBookingMapper;
import org.reussite.appui.support.dashbaord.mapper.TagMapper;
import org.reussite.appui.support.dashbaord.mapper.TeacherAvailabilityMapper;
import org.reussite.appui.support.dashbaord.mapper.TeacherProfileMapper;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.domain.StudentBooking;
import org.reussite.appui.support.dashboard.domain.Tag;
import org.reussite.appui.support.dashboard.domain.TeacherAvailability;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.exceptions.ApplicationException;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.model.CourseEntity;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.model.SubjectEntity;
import org.reussite.appui.support.dashboard.model.TagEntity;
import org.reussite.appui.support.dashboard.model.TeacherAvailabilityEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils.DateTimeFormats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;


@ApplicationScoped
public class TeacherAvailabilityService {

	@Inject ManagedExecutor executor;
	@ConfigProperty(name="reussite.appui.conference.enabled")
	protected boolean conferenceServerAvailable=true;
	@ConfigProperty(name="reussite.appui.conference.baseUrl")
	protected String videoServerUrl;
	@ConfigProperty(name="reussite.appui.conference.authKey")
	protected String videoServerAuthKey;

	@Inject
	TeacherProfileMapper teacherProfileMapper;
	
	@Inject
	protected TeacherAvailabilityMapper mapper;
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());
	@Inject TransactionManager tm;

	@Inject
	@RestClient
	protected StudentBookingService studentBookingService;
	@Inject
	@RestClient
	protected TeacherProfileService teacherProfileService;
	@Inject
	@RestClient
	protected ScheduleService scheduleService;
	
	@Inject
	@RestClient
	protected TagService tagService;
	@Inject
	protected StudentBookingMapper studentBookingMapper;
	@Inject
	protected TagMapper tagMapper;
	@Inject
	protected ScheduleMapper scheduleMapper;
	
	public ResultPage<TeacherAvailability> searchTeacherAvailabilities(String tag,String tenantKey,String firstName, String teacherId, String sortParams, Integer size, Integer page,
			String startDate, String endDate) {
		logger.info("Searching with tenant:{}",tenantKey);
			Sort sort=SearchUtils.getAbsoluteSort(sortParams);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormats.DATETIME_FORMAT);
			ZonedDateTime start=ZonedDateTime.parse(startDate,formatter);
			ZonedDateTime end=ZonedDateTime.parse(endDate,formatter);
			PanacheQuery<TeacherAvailabilityEntity>  query=null;
			String tagParam=(StringUtils.isEmpty(tag) || StringUtils.isEmpty(tag.trim()))?"null":tag.trim().toLowerCase();

			if(StringUtils.isNotBlank(teacherId)) {
				query = TeacherAvailabilityEntity.find(
						"SELECT DISTINCT c FROM TeacherAvailability c  LEFT JOIN c.tags h where (?2 ='null' OR ?2 = lower(h.name)) and c.teacherProfile.id=?1 and c.deleteDate IS NULL ",
						teacherId,tagParam);
			}
			else {
				query = TeacherAvailabilityEntity.find(
						"SELECT  c FROM TeacherAvailability c JOIN c.teacherProfile p LEFT JOIN c.tags h  where (?4 ='null' OR lower(h.name) like concat(concat('%',lower(?4),'%')) ) and (lower(p.firstName) like concat(concat('%',lower(?1),'%'))  OR  lower(p.lastName) like concat(concat('%',lower(?1),'%'))) and c.schedule.startDate > ?2 and c.schedule.startDate < ?3 and c.deleteDate IS NULL  ",
						sort,firstName,start,end,tagParam);
			}
			
			
			List<TeacherAvailabilityEntity>availabilities=query.page(page,size).list();
			List<TeacherAvailability> avs= new ArrayList<TeacherAvailability>();
		 	for(TeacherAvailabilityEntity availability:availabilities) {
		 		avs.add(mapper.toDomain(availability));
		 	}
		 	ResultPage<TeacherAvailability> resultPage= new ResultPage<TeacherAvailability>(page, query.pageCount(), query.count(),avs);
		return resultPage;
	}
	
	public TeacherAvailability getAvailability(String availabilityId) {
		logger.info("Searching with availability Id:{}",availabilityId);
			TeacherAvailabilityEntity availabilityEntity=TeacherAvailabilityEntity.findById(availabilityId);
			 if(availabilityEntity==null) {
					throw new NoSuchElementException(TeacherAvailability.class,availabilityId);
			 }
			 logger.info("TeacherAvailability found in db :{}",availabilityEntity);
			 TeacherAvailability availability= mapper.toDomain(availabilityEntity);
		return availability;
	}
	public List<StudentBooking> findStudentBookings(List<String> ids){
		List<StudentBookingEntity>  entities= StudentBookingEntity.find("id in ?1",ids).list();
	 	List<StudentBooking>bookings=entities.stream().map(studentBookingMapper::toDomain).collect(Collectors.toList());
	 	return bookings;
	}

	public List<StudentBooking> searchStudentBooking(String tenantKey,String availabilityId) {
		logger.info("Searching bookings for availability:{}",availabilityId);
			List<StudentBookingEntity>bookingEntities=StudentBookingEntity.find("teacherAvailability.id=?1", availabilityId).list();
			List<StudentBooking>bookings=bookingEntities.stream().map(studentBookingMapper::toDomain).collect(Collectors.toList());
			return bookings;
	}
	
	
	protected String createConferenceCreationUrl(TeacherAvailabilityEntity availability) {
		String firstName=availability.teacherProfile.firstName;
		String lastName=availability.teacherProfile.lastName;
		String meetingId=availability.conferenceId;
		String base="name="+encode(firstName)+"+"+encode(lastName+"'s Classroom")+"&";
		base=base+"meetingID="+meetingId;
		base=base+"&attendeePW="+availability.studentPassword+"&moderatorPW="+availability.teacherPassword;
		String checksum=DigestUtils.sha1Hex("create"+base+videoServerAuthKey);
		String baseUrl=videoServerUrl+"/create?"+base+"&checksum="+checksum;
		return baseUrl;
	}


	private String encode(String t) {
		try {
			return URLEncoder.encode(t,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
		}
		return t;
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
	
	
	protected	TeacherAvailabilityEntity createAvailability(TeacherProfileEntity profile, ScheduleEntity schedule,TeacherAvailability availability) {
		TeacherAvailabilityEntity av= new TeacherAvailabilityEntity();
		av.conferenceId=RandomStringUtils.randomAlphanumeric(5);
		av.studentPassword=RandomStringUtils.randomAlphanumeric(5);
		av.teacherPassword=RandomStringUtils.randomAlphanumeric(5);
		av.schedule=schedule;
		av.teacherProfile=profile;
		av.conferenceUrl=createJoinConferenceUrl(av);
		av.persistAndFlush();
		logger.info("Availability created:{}",av);
		return av;
	}
	@Transactional 
	public TeacherAvailability registerAvailability(TeacherAvailability availability) {
		logger.info("Creating availability:{}",availability);
		TeacherProfileEntity profile=getTeacherProfile(availability.getTeacherProfile().getId());
		logger.info("Teacher {} found while creating availability ", availability.getTeacherProfile().getId());
		ScheduleEntity schedule=getSchedule(availability.getSchedule().getId());
		logger.info("Schedule {} found while creating availability ", schedule);
		long overlap=TeacherAvailabilityEntity.countByTeacherProfileAndSchedule(profile,schedule);
		if(overlap>0) {
			logger.info("Overlap detected while creating availability ");
			 throw new AvailabilityOverlapException(profile.id,"");
		}
		TeacherAvailabilityEntity entity=createAvailability(profile,schedule, availability);
		logger.info("Availability creation completed successfully: {}  ", entity);
		return mapper.toDomain(entity);
	}
	private ScheduleEntity getSchedule(String id) {
		logger.info("Fetching schedule:{}",id);
		Schedule schedule=scheduleService.getSchedule(id);
		ScheduleEntity existing=ScheduleEntity.findById(id);
		if(existing==null) {
			ScheduleEntity entity=scheduleMapper.toEntity(schedule);
			SubjectEntity subject=SubjectEntity.findById(entity.course.subject.id);
			if(subject==null) {
				entity.course.subject.persistAndFlush();
			}else {
				entity.course.subject=subject;
			}
			CourseEntity course=CourseEntity.findById(entity.course.id);
			if(course==null) {
				entity.course.persistAndFlush();
			}else {
				entity.course=course;
			}
			entity.persistAndFlush();
			logger.info("Schedule fetched from remote service:{}",entity);
			
			return entity;
		}
		scheduleMapper.updateModel(schedule, existing);
		existing.persistAndFlush();
		logger.info("Existing schedule updated :{}",existing);
		return existing;
	}
	
	private TeacherProfileEntity getTeacherProfile(String teacherId) {
		TeacherProfile teacherProfile=teacherProfileService.getTeacherProfile(teacherId);
		TeacherProfileEntity existing=TeacherProfileEntity.findById(teacherId);
		if(existing==null) {
			TeacherProfileEntity entity=teacherProfileMapper.toEntity(teacherProfile);
			entity.persistAndFlush();
			return entity;
		}
		existing.activationDate=teacherProfile.getActivationDate();
		existing.firstName=teacherProfile.getFirstName();
		existing.lastName=teacherProfile.getLastName();
		existing.conferenceUrl=teacherProfile.getConferenceUrl();
		existing.phoneNumber=teacherProfile.getPhoneNumber();
		existing.persistAndFlush();
		return existing;
	}

	



	private StudentBookingEntity getStudentBooking(String bookingId) {
		StudentBooking studentBooking=studentBookingService.getStudentBooking(bookingId);
		StudentBookingEntity existing=StudentBookingEntity.findById(bookingId);
		if(existing==null) {
			StudentBookingEntity entity=studentBookingMapper.toEntity(studentBooking);
			StudentProfileEntity profile=StudentProfileEntity.findById(studentBooking.getStudentProfile().getId());
			if(profile!=null) {
				entity.studentProfile=profile;
			}else {
				entity.studentProfile.persist();
			}
			entity.persistAndFlush();
			return entity;
		}
		return existing;
	}
	@Transactional
	public TeacherAvailability updateTeacherAvailability(String tenantKey,String id,TeacherAvailability body) {
		 logger.info("Processing request for patching teacher availability:{} in tenant {} ",id,tenantKey);

		TeacherAvailabilityEntity availability=TeacherAvailabilityEntity.findById(id);

		 if(availability==null) {
				throw new NoSuchElementException(TeacherAvailability.class,body.getId());
		 }
		 logger.info("TeacherAvailability found in db :{}",availability);

		
		 if(StringUtils.isNotBlank(availability.duration)) {
			 availability.duration=(body.getDuration());
		 }
		 if(body.getSchedule()!=null) {
				String scheduleId=body.getSchedule().getId();
				ScheduleEntity schedule=ScheduleEntity.findById(scheduleId);
				long overlap=TeacherAvailabilityEntity.countByTeacherProfileAndSchedule(availability.teacherProfile,schedule);
				if(overlap>0) {
					 throw new AvailabilityOverlapException(availability.teacherProfile.id,"");
				}
				availability.schedule=schedule;
		 }
		 availability.lastUpdateDate=TimeUtils.getCurrentTime();
		 TeacherAvailabilityEntity.persist(availability);
		 return mapper.toDomain(availability);
	}

	@Transactional
	public TeacherAvailability assignAssistant(String tenantKey,String teacherId, String availablityId) {
		 logger.info("Assigning assistant :{} to availability:{}",teacherId,availablityId);
		 TeacherProfileEntity teacher=TeacherProfileEntity.findById(teacherId);
		 TeacherAvailabilityEntity availability=TeacherAvailabilityEntity.findById(availablityId);
		 availability.assistants.add(teacher);
		 availability.persistAndFlush();
		 return mapper.toDomain(availability);
    }

	@Transactional
	public TeacherAvailability unassignAssistant(String tenantKey,String teacherId, String availablityId) {
		 logger.info("Unassigning assiatnt :{} from availability:{}",teacherId,availablityId);
		 TeacherAvailabilityEntity availability=TeacherAvailabilityEntity.findById(availablityId);
		 TeacherProfileEntity deleteMe=null;
		 for(TeacherProfileEntity profile:availability.assistants) {
			 if(profile.id.equals(teacherId)) {
				 deleteMe=profile;
			 }
		 }
		 if(deleteMe!=null) {
			 availability.assistants.remove(deleteMe);
		 }
		 availability.persistAndFlush();
		 return mapper.toDomain(availability);
    }



	public TeacherAvailability tagTeacherAvailability(String tenantKey, String availabilityId, String tagId) {
		TagEntity existing=getTag(tagId);
		TeacherAvailabilityEntity availability=TeacherAvailabilityEntity.findById(availabilityId);
		availability.tags.add(existing);
		availability.persist();
		 return mapper.toDomain(availability);
	}
	
	private TagEntity getTag(String tagId) {
		Tag tag=tagService.getTag(tagId);
		TagEntity tagEntity=tagMapper.toEntity(tag);
		TagEntity existing=TagEntity.findById(tagId);
		if(existing!=null) {
			existing.name=tagEntity.name;
			existing.url=tagEntity.url;
		}else {
			existing=tagEntity;
		}
		existing.persistAndFlush();
		return existing;
	}

	public TeacherAvailability untagTeacherAvailability(String tenantKey, String availabilityId, String tagId) {
		TagEntity existing=getTag(tagId);
		TeacherAvailabilityEntity availability=TeacherAvailabilityEntity.findById(availabilityId);
		TagEntity deleteMe=null;
		for(TagEntity t:availability.tags) {
			if(t.id.equals(existing.id)) {
				deleteMe=t;
			}
		}
		if(deleteMe!=null) {
			availability.tags.remove(deleteMe);
		}
		availability.persist();
		return mapper.toDomain(availability);
	}


	protected String createJoinConferenceUrl(TeacherAvailabilityEntity availability, StudentBookingEntity booking) {
		logger.info("Creating conference URL with :{} and {}",availability,booking);
		String firstName=booking==null?availability.teacherProfile.firstName:booking.studentProfile.firstName;
		String lastName=booking==null?availability.teacherProfile.lastName:booking.studentProfile.lastName;
		String meetingId=availability.conferenceId;
		
		String base="fullName="+encode(firstName)+"+"+encode(lastName+"'s Classroom")+"&";
		base=base+"joinViaHtml5=true&meetingID="+meetingId;
		base=base+"&password="+availability.teacherPassword;
		String checksum=DigestUtils.sha1Hex("join"+base+videoServerAuthKey);
		String baseUrl=videoServerUrl+"/join?"+base+"&checksum="+checksum;
		logger.info("Join URL computed:{}",baseUrl);
		return baseUrl;
	}
	

	public TeacherAvailability unassignTeacher(String tenantKey,String bookingId, String availablityId) {
		 StudentBookingEntity booking=getStudentBooking(bookingId);
		 logger.info("Assigning booking:{} to availability:{}",bookingId,availablityId);
		 if(booking==null) {
				throw new NoSuchElementException(StudentBooking.class,bookingId);
		 }
		
		 TeacherAvailabilityEntity previous=booking.teacherAvailability;
		 TeacherAvailabilityEntity availability=TeacherAvailabilityEntity.findById(availablityId);
		 if(availability==null) {
			 throw new ApplicationException("No teacher availability assigned");
		 }
		
		 booking.teacherAvailability=null;
		 booking.teacherAssignedDate=TimeUtils.getCurrentTime();
		 booking.conferenceUrl=null;
		 booking.persistAndFlush();
		 
		 long count=StudentBookingEntity.countByTeacherAvailability(availability.id);
		 availability.studentCount=count;
		 availability.persistAndFlush();
		 if(previous!=null) {
			 count=StudentBookingEntity.countByTeacherAvailability(previous.id);
			 previous.studentCount=count;
			 previous.lastUpdateDate=TimeUtils.getCurrentTime();
			 previous.persistAndFlush();
		 }
		 return mapper.toDomain(availability);
    }

	@Transactional
	public TeacherAvailability assignTeacher(String tenantKey,String bookingId, String availablityId) {
		 StudentBookingEntity booking=getStudentBooking(bookingId);
		 logger.info("Assigning booking:{} to availability:{}",bookingId,availablityId);
		 if(booking==null) {
				throw new NoSuchElementException(StudentBooking.class,bookingId);
		 }
	
		 TeacherAvailabilityEntity previous=booking.teacherAvailability;
		 TeacherAvailabilityEntity availability=TeacherAvailabilityEntity.findById(availablityId);
		 if(availability==null) {
			 throw new ApplicationException("No teacher availability assigned");
		 }
	
		 booking.teacherAvailability=availability;
		 booking.teacherAssignedDate=TimeUtils.getCurrentTime();
		 booking.conferenceUrl=createJoinConferenceUrl(availability,booking);
		 booking.persistAndFlush();
		 logger.info("Persisted booking:{}",booking);
		 long count=StudentBookingEntity.countByTeacherAvailability(availability.id);
		 availability.studentCount=count;
		 availability.persistAndFlush();
		 if(previous!=null) {
			 count=StudentBookingEntity.countByTeacherAvailability(previous.id);
			 previous.studentCount=count;
			 previous.lastUpdateDate=TimeUtils.getCurrentTime();
			 previous.persistAndFlush();
		 }
		 return mapper.toDomain(availability);
    }
}
