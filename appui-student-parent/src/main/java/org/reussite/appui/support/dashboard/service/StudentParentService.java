package org.reussite.appui.support.dashboard.service;


import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.reussite.appui.support.dashbaord.mapper.StudentParentMapper;
import org.reussite.appui.support.dashbaord.utils.OAuth2Constants;
import org.reussite.appui.support.dashbaord.utils.PhoneUtils;
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.domain.TokenResponse;
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

	@Inject
    @RestClient
	protected KeycloakService keycloakService;
	@ConfigProperty(name = "reussite.appui.keycloak.client.name") 
	protected String keycloakClientName;
	@ConfigProperty(name = "reussite.appui.keycloak.client.secret") 
	protected String keycloakClientSecret;


	public StudentParent registerParent(StudentParent studentParent) {
		logger.info("Creating parent :{} --> {}",studentParent);
		StudentParentEntity parent=parentMapper.toEntity(studentParent);
		String phone=PhoneUtils.validate(parent.phoneNumber,parent.countryCode);
		StudentParentEntity p=StudentParentEntity.findByPhoneNumber(phone);
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
		String response=keycloakService.register(parent.phoneNumber);
		logger.info("Parent registration triggered in Keycloak:{}, Persisting parent:{}",response,parent);
		parent.persistAndFlush();
		StudentParent result=parentMapper.toDomain(parent);
		return result;
	}

	@Transactional
	public TokenResponse activateParent(String id, String activationCode, String client, String refreshToken) {
		StudentParentEntity parent= StudentParentEntity.findById(id);
		logger.info("Parent found in db: {}",parent);
		logger.info("Preparing to send activation request to Keycloak:{} ");
		parent.lastUpdateDate=TimeUtils.getCurrentTime();
		parent.activationDate=TimeUtils.getCurrentTime();
		TokenResponse token=null;
		if(StringUtils.isBlank(refreshToken)) {
			token=keycloakService.activate(OAuth2Constants.PASSWORD.asLowerCase(),parent.phoneNumber,activationCode,keycloakClientName,keycloakClientSecret,null);
		}else {
			token=keycloakService.activate(OAuth2Constants.PASSWORD.asLowerCase(),parent.phoneNumber,null,keycloakClientName,keycloakClientSecret,refreshToken);
		}
		logger.info("Activation response from  Keycloak:{} ",token);

		parent.persistAndFlush();
		return token;
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
		 if(StringUtils.isNotBlank(body.getEmail()) ) {
			 profile.email=(body.getEmail());
		 }
		 if(StringUtils.isNotBlank(body.getLanguage())) {
			 profile.language=(body.getLanguage()).toUpperCase();
		 }
		
		 profile.lastUpdateDate=TimeUtils.getCurrentTime();
		 StudentParentEntity.persist(profile);
		 return parentMapper.toDomain(profile);
		
	}
}
