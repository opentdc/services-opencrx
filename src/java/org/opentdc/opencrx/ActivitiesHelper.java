/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Arbalo AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.opentdc.opencrx;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.activity1.cci2.ActivityQuery;
import org.opencrx.kernel.activity1.cci2.ActivityTrackerQuery;
import org.opencrx.kernel.activity1.cci2.ActivityTypeQuery;
import org.opencrx.kernel.activity1.cci2.ActivityWorkRecordQuery;
import org.opencrx.kernel.activity1.cci2.ResourceAssignmentQuery;
import org.opencrx.kernel.activity1.jmi1.AccountAssignmentActivityGroup;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityCreator;
import org.opencrx.kernel.activity1.jmi1.ActivityLinkTo;
import org.opencrx.kernel.activity1.jmi1.ActivityTracker;
import org.opencrx.kernel.activity1.jmi1.ActivityType;
import org.opencrx.kernel.activity1.jmi1.ActivityWorkRecord;
import org.opencrx.kernel.activity1.jmi1.AddWorkAndExpenseRecordResult;
import org.opencrx.kernel.activity1.jmi1.NewActivityParams;
import org.opencrx.kernel.activity1.jmi1.NewActivityResult;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.activity1.jmi1.ResourceAddWorkRecordParams;
import org.opencrx.kernel.activity1.jmi1.ResourceAssignment;
import org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

/**
 * Sample helpers for project management.
 *
 */
public abstract class ActivitiesHelper {

    /**
     * Get customer project groups and restrict to account if specified.
     * 
     * @param activitySegment
     * @param account
     * @return
     */
    public static List<ActivityTracker> getCustomerProjectGroups(
    	org.opencrx.kernel.activity1.jmi1.Segment activitySegment,
    	org.opencrx.kernel.account1.jmi1.Account account
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(activitySegment);
    	ActivityTrackerQuery activityTrackerQuery = 
    		(ActivityTrackerQuery)pm.newQuery(ActivityTracker.class);
    	activityTrackerQuery.forAllDisabled().isFalse();
    	activityTrackerQuery.activityGroupType().equalTo(ACTIVITY_GROUP_TYPE_PROJECT);
    	activityTrackerQuery.thereExistsAssignedAccount().accountRole().equalTo(ACCOUNT_ROLE_CUSTOMER);
    	if(account != null) {
    		activityTrackerQuery.thereExistsAssignedAccount().thereExistsAccount().equalTo(account);
    	}
    	activityTrackerQuery.orderByName().ascending();
    	return activitySegment.getActivityTracker(activityTrackerQuery);
    }

    /**
     * Create a customer project group.
     * 
     * @param activitySegment
     * @param name
     * @param description
     * @param customer
     */
    public static ActivityTracker createCustomerProjectGroup(
    	PersistenceManager pm,
    	org.opencrx.kernel.activity1.jmi1.Segment activitySegment,
    	String name,
    	String description,
    	org.opencrx.kernel.account1.jmi1.Account customer
    ) {
    	try {
    	    pm.currentTransaction().begin();
    	    // Activity tracker
    	    ActivityTracker activityTracker = null;
    	    {
    	        activityTracker = pm.newInstance(ActivityTracker.class);
    	        activityTracker.setName(name);
    	        activityTracker.setDescription(description);
    	        activityTracker.setActivityGroupType(ACTIVITY_GROUP_TYPE_PROJECT);
    	        activitySegment.addActivityTracker(
    	            org.opencrx.kernel.utils.Utils.getUidAsString(), 
    	            activityTracker
    	        );
    	    }
    	    // Activity creator
    	    ActivityCreator activityCreator = null;
    	    {
    	    	ActivityTypeQuery activityTypeQuery = 
		    		(ActivityTypeQuery)pm.newQuery(ActivityType.class);
		        activityTypeQuery.name().elementOf(Arrays.asList("Bugs + Features", "Incidents"));
		        List<ActivityType> activityTypes = activitySegment.getActivityType(activityTypeQuery);
		        ActivityType incidentType = activityTypes.isEmpty() ? null : activityTypes.iterator().next();
    	    	activityCreator = pm.newInstance(ActivityCreator.class);
    	    	activityCreator.setName(name);
    	    	activityCreator.setDescription(description);
    	    	activityCreator.getActivityGroup().add(activityTracker);
    	    	activityCreator.setActivityType(incidentType);
    	    	activityCreator.setIcalClass(ICAL_CLASS_NA);
    	    	activityCreator.setIcalType(ICAL_TYPE_VEVENT);
    	    	activityCreator.setPriority((short)0);
    	    	activitySegment.addActivityCreator(
    	    		org.opencrx.kernel.utils.Utils.getUidAsString(),
    	    		activityCreator
    	    	);
    	    }
    	    // Account assignment
    	    {
    	    	AccountAssignmentActivityGroup accountAssignment = pm.newInstance(AccountAssignmentActivityGroup.class);
    	    	accountAssignment.setName(customer.getFullName());
    	    	accountAssignment.setAccount(customer);
    	    	accountAssignment.setAccountRole(ACCOUNT_ROLE_CUSTOMER);
    	    	activityTracker.addAssignedAccount(
    	    		org.opencrx.kernel.utils.Utils.getUidAsString(),
    	    		accountAssignment
    	    	);
    	    }
    	    pm.currentTransaction().commit();
        	return activityTracker;
    	} catch(Exception e) {
			new ServiceException(e).log();
    	    try {
    	        pm.currentTransaction().rollback();
    	    } catch(Exception ignore) {}
    	}
    	return null;
    }
    
    /**
     * Get customer projects for the given customer project group.
     * 
     * @param customerProjectGroup
     * @return
     */
    public static List<Activity> getCustomerProjects(
    	ActivityTracker customerProjectGroup,
    	boolean rootsOnly
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(customerProjectGroup);
    	ActivityQuery activityQuery = (ActivityQuery)pm.newQuery(Activity.class);
    	activityQuery.forAllDisabled().isFalse();
    	activityQuery.orderByName().ascending();
    	if(rootsOnly) {
    		activityQuery.forAllActivityLinkTo().activityLinkType().notEqualTo(ACTIVITY_LINK_TYPE_IS_CHILD_OF);
    	}
    	return customerProjectGroup.getFilteredActivity(activityQuery);
    }

	/**
	 * Create a customer project.
	 * 
	 * @param customerProjectGroup
	 * @return
	 */
    public static Activity createCustomerProject(
    	PersistenceManager pm,
		ActivityTracker customerProjectGroup,
		String name,
		String description,
		String detailedDescription,
		Date scheduledStart,
		Date scheduledEnd,
		short priority,
		Activity parentProject
	) {
		ActivityCreator customerProjectCreator = null;
		for(ActivityCreator activityCreator: customerProjectGroup.<ActivityCreator>getActivityCreator()) {
			if(activityCreator.getActivityType().getActivityClass() == ACTIVITY_CLASS_INCIDENT) {
				customerProjectCreator = activityCreator;
				break;
			}
		}
		if(customerProjectCreator != null) {
			try {
				pm.currentTransaction().begin();
				NewActivityParams newActivityParams = Structures.create(
					NewActivityParams.class,
					Datatypes.member(NewActivityParams.Member.name, name),
					Datatypes.member(NewActivityParams.Member.description, description),
					Datatypes.member(NewActivityParams.Member.detailedDescription, detailedDescription),
					Datatypes.member(NewActivityParams.Member.scheduledStart, scheduledStart),
					Datatypes.member(NewActivityParams.Member.scheduledEnd, scheduledEnd),
					Datatypes.member(NewActivityParams.Member.priority, priority),
					Datatypes.member(NewActivityParams.Member.icalType, ICAL_TYPE_NA)
				);
				NewActivityResult newActivityResult = customerProjectCreator.newActivity(newActivityParams);
				pm.currentTransaction().commit();
				Activity project = newActivityResult.getActivity();
				if(parentProject != null) {
					pm.currentTransaction().begin();
					ActivityLinkTo activityLinkTo = pm.newInstance(ActivityLinkTo.class);
					activityLinkTo.setName(parentProject.getName());
					activityLinkTo.setLinkTo(parentProject);
					activityLinkTo.setActivityLinkType(ACTIVITY_LINK_TYPE_IS_CHILD_OF);
					project.addActivityLinkTo(
						Utils.getUidAsString(),
						activityLinkTo
					);
					pm.currentTransaction().commit();
				}
				return project;
			} catch(Exception e) {
				new ServiceException(e).log();
				try {
					pm.currentTransaction().rollback();
				} catch(Exception ignore) {}
			}
		}
		return null;
    }

    /**
     * Get project resources.
     * 
     * @param activitySegment
     * @return
     */
    public static List<Resource> getProjectResources(
    	Activity project
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(project);
    	ResourceAssignmentQuery resourceAssignmentQuery = (ResourceAssignmentQuery)pm.newQuery(ResourceAssignment.class);
    	resourceAssignmentQuery.forAllDisabled().isFalse();
    	resourceAssignmentQuery.orderByName().ascending();
    	resourceAssignmentQuery.resourceRole().equalTo(RESOURCE_ROLE_MEMBER);
    	List<Resource> resources = new ArrayList<Resource>();
    	List<ResourceAssignment> assignments = project.getAssignedResource(resourceAssignmentQuery);
    	for(ResourceAssignment assignment: assignments) {
    		resources.add(assignment.getResource());
    	}
    	return resources;
    }

    /**
     * Get activity work records for given resource.
     * 
     * @param resource
     * @return
     */
    public static List<WorkAndExpenseRecord> getWorkRecords(
    	Resource resource
    ) {
    	PersistenceManager pm = JDOHelper.getPersistenceManager(resource);
    	ActivityWorkRecordQuery workRecordQuery =
    		(ActivityWorkRecordQuery)pm.newQuery(ActivityWorkRecord.class);
    	workRecordQuery.orderByStartedAt().ascending();
    	return resource.getWorkReportEntry(workRecordQuery);
    }

    /**
     * Create work record for given resource and activity.
     * 
     * @param resource
     * @param name
     * @param description
     * @param startAt
     * @param durationHours
     * @param durationMinutes
     * @param isBillable
     * @param rate
     * @param rateCurrency
     * @param activity
     * @return
     */
    public static WorkAndExpenseRecord createWorkRecord(
    	PersistenceManager pm,
    	Resource resource,
    	String name,
    	String description,
    	Date startAt,
    	Integer durationHours,
    	Integer durationMinutes,
    	Boolean isBillable,
    	BigDecimal rate,
    	short rateCurrency,
    	Activity activity
    ) {
    	try {
    		pm.currentTransaction().begin();
    		ResourceAddWorkRecordParams addWorkRecordParams = Structures.create(
    			ResourceAddWorkRecordParams.class,
    			Datatypes.member(ResourceAddWorkRecordParams.Member.activity, activity),
    			Datatypes.member(ResourceAddWorkRecordParams.Member.name, name),
    			Datatypes.member(ResourceAddWorkRecordParams.Member.description, description),
    			Datatypes.member(ResourceAddWorkRecordParams.Member.startAt, startAt),
    			Datatypes.member(ResourceAddWorkRecordParams.Member.durationHours, durationHours),
    			Datatypes.member(ResourceAddWorkRecordParams.Member.durationMinutes, durationMinutes),
    			Datatypes.member(ResourceAddWorkRecordParams.Member.isBillable, isBillable),
    			Datatypes.member(ResourceAddWorkRecordParams.Member.rate, rate),
    			Datatypes.member(ResourceAddWorkRecordParams.Member.rateCurrency, rateCurrency),
    			Datatypes.member(ResourceAddWorkRecordParams.Member.recordType, (short)0)        			
    		);
    		AddWorkAndExpenseRecordResult addWorkRecordResult = resource.addWorkRecord(addWorkRecordParams);
    		pm.currentTransaction().commit();
    		return addWorkRecordResult.getWorkRecord();
    	} catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
    	}
    	return null;
    }
    
	public static final short ICAL_CLASS_NA = 0;
	public static final short ICAL_TYPE_VEVENT = 1;
	public static final short ACTIVITY_GROUP_TYPE_PROJECT = 40;
	public static final short ACCOUNT_ROLE_CUSTOMER = 100;
	public static final short ACTIVITY_CLASS_INCIDENT = 2;
	public static final short ICAL_TYPE_NA = 0;
	public static final String RESOURCE_CATEGORY_PROJECT = "Project";
	public static final short ACTIVITY_LINK_TYPE_IS_CHILD_OF = 99;
	public static final short ACTIVITY_PRIORITY_NA = 0;
	public static final short RESOURCE_ROLE_MEMBER = 2;
    
}
