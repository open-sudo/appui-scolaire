package org.reussite.appui.support.dashboard.model;

import java.util.Collection;

public class ResultPage<K> {
	public long currentPage;
	public long numberOfPages;
	public long totalCount;
	
	public Collection<K> content;
	
	public ResultPage() {}
	
	public ResultPage(Collection<K> content){
		this.content=content;
	}

	public ResultPage(long currentPage, long numberOfPages, long totalCount, Collection<K> content) {
		this.currentPage = currentPage;
		this.numberOfPages = numberOfPages;
		this.totalCount = totalCount;
		this.content = content;
	}
	
}
