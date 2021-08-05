package org.reussite.appui.support.dashboard.service;


import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.reussite.appui.support.dashbaord.mapper.StudentParentMapper;
import org.reussite.appui.support.dashbaord.utils.PhoneUtils;
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.exceptions.ApplicationException;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class StudentParentService {
	

	@Inject
	protected StudentParentMapper parentMapper;
	
		
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());



	public StudentParent registerParent(StudentParent studentParent) {
		logger.info("Creating parent :{} --> {}",studentParent);
		StudentParentEntity parent=parentMapper.toEntity(studentParent);
		String phone=PhoneUtils.validate(parent.phoneNumber,parent.countryCode);
		StudentParentEntity p=StudentParentEntity.findByPhoneNumber(parent.phoneNumber);
		if(p!=null) {
			logger.info("Existing parent found:{}",p);
			parent=p;
		}else {
			logger.info("No existing parent found with phone:{}",phone);
			parent.phoneNumber=phone;
		}
		if(StringUtils.isBlank(parent.email)) {
			parent.email=parent.phoneNumber+"@phone.com";
		}
		parent.activationDate=null;
		String activatioCode=RandomStringUtils.random(4, new char[] {'0','1','2','3','4','5','6','7','8','9'});
		parent.activationCode=activatioCode;
		
		logger.info("Persisting parent:{}",parent);
		parent.persistAndFlush();
		StudentParent result=parentMapper.toDomain(parent);
		return result;
	}

	@Transactional
	public String activateParent(String id, String activationCode) {
		StudentParentEntity parent= StudentParentEntity.findById(id);
		logger.info("Parent found in db: {}",parent);
		if(!parent.activationCode.equalsIgnoreCase(activationCode)) {
			throw new ApplicationException(StudentParent.class.getCanonicalName());
		}
		HashSet<String>groups=new HashSet<String>();

		groups.add("USER");
		/*String jwt =Jwt.issuer(jwtIssuer).expiresIn(Duration.ofDays(30)) 
		             .upn(parent.phoneNumber) 
		             .groups(groups) 
		             .sign();*/
		parent.lastUpdateDate=TimeUtils.getCurrentTime();
		parent.activationDate=TimeUtils.getCurrentTime();
		parent.persistAndFlush();
		return "OK";
	}

	public ResultPage<StudentParent>  searchStudentParents(String firstName, String sortParams, Integer size, Integer page) {
		logger.info("Searching student parent with :{}, query{}, sort:{}. Tag:{}",firstName,sortParams);
		Sort sort=SearchUtils.getAbsoluteSort(sortParams);

		
		logger.info("Panache sort computed:{},{}",sort.getColumns().get(0).getName(),sort.getColumns().get(0).getDirection().name());
		PanacheQuery<StudentParentEntity> query = StudentParentEntity.find(
				"SELECT  c FROM StudentParent  c  where   (lower(c.email) like concat(concat('%',lower(?1),'%')) OR lower(c.firstName) like concat(concat('%',lower(?1),'%'))  OR  lower(c.lastName) like concat(concat('%',lower(?1),'%'))) and c.deleteDate IS NULL ",
				sort,firstName);
		
		List<StudentParent> result=query.page(page, size).list().stream().map(parentMapper::toDomain).collect(Collectors.toList());
		ResultPage<StudentParent> resultPage= new ResultPage<StudentParent>(page,query.pageCount(),query.count(),result);
		return resultPage;
	}
	
	public StudentParent updateParentProfile( StudentParent body) {
		StudentParentEntity profile=StudentParentEntity.findById(body.getId());
		 if(profile==null) {
				throw new NoSuchElementException(StudentParent.class,body.getId());
		 }
	
		 if(StringUtils.isNotBlank(body.getFirstName())) {
			 profile.firstName=(body.getFirstName());
		 }
		 if(StringUtils.isNotBlank(body.getLastName())) {
			 profile.lastName=(body.getLastName());
		 }
		 if(body.getCountryCode()>0) {
			 profile.countryCode=body.getCountryCode();
		 }
		 if(StringUtils.isNotBlank(body.getPhoneNumber()) && StringUtils.isBlank(profile.phoneNumber)) {
			 profile.phoneNumber=(body.getPhoneNumber());
		 }
		 if(StringUtils.isNotBlank(body.getEmail()) && StringUtils.isBlank(profile.email)) {
			 profile.phoneNumber=(body.getEmail());
		 }
		 if(StringUtils.isNotBlank(body.getLanguage())) {
			 profile.language=(body.getLanguage()).toUpperCase();
		 }
	
		 profile.lastUpdateDate=TimeUtils.getCurrentTime();
		 StudentParentEntity.persist(profile);
		 return parentMapper.toDomain(profile);
		
	}
}
