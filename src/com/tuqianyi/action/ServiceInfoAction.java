package com.tuqianyi.action;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.opensymphony.xwork2.ActionContext;
import com.taobao.api.domain.ArticleUserSubscribe;
import com.tuqianyi.db.Dao;

public class ServiceInfoAction extends ActionBase{

	private long mergedItemsCount;
	private Date serviceEnd;
	
	public String execute() throws Exception
	{
		mergedItemsCount = Dao.INSTANCE.getMergedItemsCount(getUser());
		List<ArticleUserSubscribe> subs = (List<ArticleUserSubscribe>)ActionContext.getContext().getSession().get(SUBSCRIPTION);
		if (subs != null)
		{
			for (ArticleUserSubscribe sub : subs)
			{
				Date deadline = sub.getDeadline();
				if (serviceEnd == null)
				{
					serviceEnd = deadline;
				}
				else
				{
					if (deadline.after(serviceEnd))
					{
						serviceEnd = deadline;
					}
				}
			}
		}
		return SUCCESS;
	}

	public void setMergedItemsCount(long mergedItemsCount) {
		this.mergedItemsCount = mergedItemsCount;
	}

	public long getMergedItemsCount() {
		return mergedItemsCount;
	}

	public void setServiceEnd(Date serviceEnd) {
		this.serviceEnd = serviceEnd;
	}

	public Date getServiceEnd() {
		return serviceEnd;
	}
	
	public long getLeft()
	{
		return (serviceEnd.getTime() - System.currentTimeMillis()) / DateUtils.MILLIS_PER_DAY;
	}
}
