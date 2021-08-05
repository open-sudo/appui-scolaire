package org.reussite.appui.support.dashboard.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang3.StringUtils;
import org.reussite.appui.support.dashboard.domain.TagRequest;
import org.reussite.appui.support.dashboard.domain.TagResponse;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.mapper.TagMapper;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.TagEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class TagService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
	protected TagMapper tagMapper;


	public TagResponse createTag(TagRequest tag) {
			logger.info("Creating tag :{}",tag);
			if(tag.getEnabled()==null) {
				tag.setEnabled(true);
			}
			TagEntity tagEntity=tagMapper.toEntity(tag);
			tagEntity.enabled=(tag.getEnabled());
			tagEntity.name=(tag.getName().replaceAll("[^a-zA-Z0-9]", ""));
			tagEntity.url=(tag.getUrl());
			tagEntity.persist();
			logger.info("Creating tag completed:{}",tagEntity);
			return tagMapper.toDomain(tagEntity);
	}
	
	
	public TagRequest updateTag(String id, TagRequest body) {
		logger.info("Executing request to update tag :{}",body);
		TagEntity tag=TagEntity.findById(id);
		if(tag==null) {
			throw new NoSuchElementException(TagRequest.class,id);
		}
		if(StringUtils.isNoneBlank(body.getName())) {
			tag.name=(body.getName().replaceAll("[^a-zA-Z0-9]", ""));
		}
		if(StringUtils.isNoneBlank(body.getUrl())) {
			tag.url=(body.getUrl());
		}
		if(body.getEnabled()!=null) {
			tag.enabled=(body.getEnabled());
		}
		tag.lastUpdateDate=(TimeUtils.getCurrentTime());
		tag.persist();
		logger.info("Executing request to update tag  completed with body :{} ",tag);
		return tagMapper.toDomain(tag);
	}
	
	public List<TagRequest> enableTags(List<String> ids) {
		logger.info("Executing request to enable tags :{} ",Arrays.deepToString(ids.toArray()));
		List<TagRequest>tags= new ArrayList<TagRequest>();
		List<TagEntity> tagEntities= new ArrayList<TagEntity>();
		for(String id:ids) {
			TagEntity tag=TagEntity.findById(id);
			if(tag!=null) {
				tag.enabled=(true);
				tag.lastUpdateDate=(TimeUtils.getCurrentTime());
				tagEntities.add(tag);
				tags.add(tagMapper.toDomain(tag));
			}
		}
		TagEntity.persist(tagEntities);
		return tags;
	}
	
    public ResultPage<TagResponse>  searchTags(@Context SecurityContext securityContext,
    		  String name, String sort, Integer size, Integer page) {
		ResultPage<TagResponse> result= searchTags( name, sort, size, page);
		return (result);
	}

	
	public ResultPage<TagResponse> searchTags( String name,String sortParams, Integer size,
			Integer page) {
		logger.info("Searching tags for  title:{}.  Sort params:{}",sortParams);
		Sort sort=SearchUtils.getAbsoluteSort(sortParams, TagRequest.class);
		PanacheQuery<TagEntity> query = TagEntity.find("select DISTINCT c from Tag c where lower(c.name) like concat('%',concat(?1,'%'))  and c.deleteDate IS NULL ",sort,name.toLowerCase());
		List<TagEntity> result=query.page(page, size).list();
		List<TagResponse> tags=new ArrayList<TagResponse>();
		result.stream().forEach(t -> tags.add(tagMapper.toDomain(t)));
		ResultPage<TagResponse> resultPage= new ResultPage<TagResponse>(page,query.pageCount(),query.count(),tags);
		logger.info("Number of tags found:{}",result.toArray().length);
		return resultPage;
	}

	public List<TagResponse>  disableTag(List<String> ids) {
		logger.info("Executing request to enable tags :{} ",Arrays.deepToString(ids.toArray()));
		List<TagResponse>tags= new ArrayList<TagResponse>();
		for(String id:ids) {
			TagEntity tag=TagEntity.findById(id);
			if(tag!=null) {
				tag.enabled=(false);
				tag.lastUpdateDate=(TimeUtils.getCurrentTime());
				tag.persistAndFlush();
				tags.add(tagMapper.toDomain(tag));
			}
		}
		return tags;
	}

}
