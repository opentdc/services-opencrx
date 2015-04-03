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
import java.util.Date;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.activity1.cci2.ActivityWorkRecordQuery;
import org.opencrx.kernel.activity1.cci2.ResourceQuery;
import org.opencrx.kernel.activity1.jmi1.Activity;
import org.opencrx.kernel.activity1.jmi1.ActivityWorkRecord;
import org.opencrx.kernel.activity1.jmi1.AddWorkAndExpenseRecordResult;
import org.opencrx.kernel.activity1.jmi1.Resource;
import org.opencrx.kernel.activity1.jmi1.ResourceAddWorkRecordParams;
import org.opencrx.kernel.activity1.jmi1.WorkAndExpenseRecord;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.w3c.spi2.Datatypes;
import org.w3c.spi2.Structures;

/**
 * Sample helpers for project management.
 *
 */
public class OpencrxHelper {
	private static String providerName = "CRX";
	private static String segmentName = "Standard";
	public static final String XRI_ACCOUNT_SEGMENT = "xri://@openmdx*org.opencrx.kernel.account1";
	public static final String XRI_DOCUMENT_SEGMENT = "xri://@openmdx*org.opencrx.kernel.document1";

	/**
	 * Get account segment.
	 * 
	 * @param pm
	 * @return
	 */
	public static org.opencrx.kernel.account1.jmi1.Segment getAccountSegment(
			PersistenceManager pm) {
		return (org.opencrx.kernel.account1.jmi1.Segment) pm
				.getObjectById(new Path(XRI_ACCOUNT_SEGMENT).getDescendant(
						"provider", providerName, "segment", segmentName));
	}

	/**
	 * Get document segment.
	 * 
	 * @param pm
	 * @return
	 */
	public static org.opencrx.kernel.document1.jmi1.Segment getDocumentSegment(
			PersistenceManager pm) {
		return (org.opencrx.kernel.document1.jmi1.Segment) pm
				.getObjectById(new Path(XRI_DOCUMENT_SEGMENT).getDescendant(
						"provider", providerName, "segment", segmentName));
	}

	/***************************** ProjectGroup ********************************************/
	/*************** list companies **************/
	/*
	 * public static ArrayList<WttData> getCompanies() { ArrayList<WttData>
	 * _companies = new ArrayList<WttData>(); List<ActivityTracker> _trackers =
	 * getCustomerProjectGroups( activitySegment, null); for (ActivityTracker
	 * _tracker : _trackers) { _companies.add(new
	 * WttData(_tracker.refGetPath().toXRI(), _tracker .getName(),
	 * WttType.COMPANY, _tracker.getDescription())); } return _companies; }
	 */

	/*************** create company **************/
	/*
	 * public static WttData createCompany(WttData wttDatum) { ActivityTracker
	 * _activityTracker = createCustomerProjectGroup( wttDatum.getTitle(),
	 * wttDatum.getDescription(), getMyOwnAccount()); return new
	 * WttData(_activityTracker.refGetPath().toXRI(),
	 * _activityTracker.getName(), WttType.COMPANY,
	 * _activityTracker.getDescription()); }
	 * 
	 * // TODO: this is a temporary solution that only works with opencrx demo
	 * // setup private static Account getMyOwnAccount() { PersistenceManager
	 * _pm = JDOHelper .getPersistenceManager(activitySegment); try { return
	 * (Account) _pm .getObjectById(new Path( XRI_ACCOUNT_SEGMENT +
	 * "/provider/CRX/segment/Standard/account/9LOJK8ZMLRI73M3XRZJP5TDHW")); }
	 * finally { _pm.close(); } }
	 */

	/*************** read company **************/
	/**
	 * Get a customer project group by XRI-ID
	 * 
	 * @param xri
	 * @return
	 */
	/*
	 * public static WttData getCompany(String xri) { PersistenceManager _pm =
	 * JDOHelper .getPersistenceManager(activitySegment); try { ActivityTracker
	 * _tracker = (ActivityTracker) _pm .getObjectById(new
	 * org.openmdx.base.naming.Path(xri)); return new
	 * WttData(_tracker.refGetPath().toXRI(), _tracker.getName(),
	 * WttType.COMPANY, _tracker.getDescription()); } finally { _pm.close(); } }
	 */

	/*
	 * private static ActivityTracker getActivityTracker(String xri) {
	 * PersistenceManager _pm = JDOHelper
	 * .getPersistenceManager(activitySegment); ActivityTracker _tracker =
	 * (ActivityTracker) _pm .getObjectById(new
	 * org.openmdx.base.naming.Path(xri)); _pm.close(); return _tracker; }
	 */

	/******************************* Project ******************************************/

	/*************** list projects **************/
	/*
	 * public static List<WttData> getProjects(String groupXri) { // for each
	 * group, get all projects ArrayList<WttData> _wttData = new
	 * ArrayList<WttData>(); List<Activity> _activities =
	 * getCustomerProjects(getActivityTracker(groupXri)); for (Activity
	 * _activity : _activities) { _wttData.add(new
	 * WttData(_activity.refGetPath().toXRI(), _activity .getName(),
	 * WttType.PROJECT, _activity.getDescription())); } return _wttData; }
	 */

	/*************** create project **************/
	/*
	 * public static WttData createProject(String groupId, WttData newProject) {
	 * ActivityTracker _activityTracker = getActivityTracker(groupId); Activity
	 * _activity = createCustomerProject(_activityTracker,
	 * newProject.getTitle(), newProject.getDescription(), null, new Date(), new
	 * Date(), (short) 0); newProject.setXri(_activity.refGetPath().toXRI());
	 * return newProject; }
	 */

	/**
	 * Create a customer project.
	 * 
	 * @param customerProjectGroup
	 * @return
	 */
	/*
	 * private static Activity createCustomerProject( ActivityTracker
	 * customerProjectGroup, String name, String description, String
	 * detailedDescription, Date scheduledStart, Date scheduledEnd, short
	 * priority) { PersistenceManager pm = JDOHelper
	 * .getPersistenceManager(customerProjectGroup); ActivityCreator
	 * customerProjectCreator = null; for (ActivityCreator activityCreator :
	 * customerProjectGroup .<ActivityCreator> getActivityCreator()) { if
	 * (activityCreator.getActivityType().getActivityClass() ==
	 * ACTIVITY_CLASS_INCIDENT) { customerProjectCreator = activityCreator;
	 * break; } } if (customerProjectCreator != null) { try {
	 * pm.currentTransaction().begin(); NewActivityParams newActivityParams =
	 * Structures.create( NewActivityParams.class, Datatypes.member(
	 * NewActivityParams.Member.name, name), Datatypes
	 * .member(NewActivityParams.Member.description, description),
	 * Datatypes.member( NewActivityParams.Member.detailedDescription,
	 * detailedDescription), Datatypes.member(
	 * NewActivityParams.Member.scheduledStart, scheduledStart),
	 * Datatypes.member( NewActivityParams.Member.scheduledEnd, scheduledEnd),
	 * Datatypes.member( NewActivityParams.Member.priority, priority),
	 * Datatypes.member(NewActivityParams.Member.icalType, ICAL_TYPE_NA));
	 * NewActivityResult newActivityResult = customerProjectCreator
	 * .newActivity(newActivityParams); pm.currentTransaction().commit(); return
	 * newActivityResult.getActivity(); } catch (Exception e) { new
	 * ServiceException(e).log(); try { pm.currentTransaction().rollback(); }
	 * catch (Exception ignore) { } } } return null; }
	 */

	/*************** read project **************/
	/*
	 * public static WttData getProject(String xri) { PersistenceManager _pm =
	 * JDOHelper .getPersistenceManager(activitySegment); try { ActivityTracker
	 * _tracker = (ActivityTracker) _pm .getObjectById(new
	 * org.openmdx.base.naming.Path(xri)); return new
	 * WttData(_tracker.refGetPath().toXRI(), _tracker.getName(),
	 * WttType.PROJECT, _tracker.getDescription()); } finally { _pm.close(); }
	 * 
	 * }
	 */

	/*************** update project **************/
	// TODO opencrx implementation of update project

	/*************** delete project **************/
	// TODO opencrx implementation of delete project

	/********************************* Resource ****************************************/

	/**
	 * Get project resources.
	 * 
	 * @param activitySegment
	 * @return
	 */
	public static List<Resource> getProjectResources(
			org.opencrx.kernel.activity1.jmi1.Segment activitySegment) {
		PersistenceManager pm = JDOHelper
				.getPersistenceManager(activitySegment);
		ResourceQuery resourceQuery = (ResourceQuery) pm
				.newQuery(Resource.class);
		resourceQuery.forAllDisabled().isFalse();
		resourceQuery.orderByName().ascending();
		return activitySegment.getResource(resourceQuery);
	}

	/**
	 * Create project resource for given contact.
	 * 
	 * @param contact
	 * @return
	 */
	public static Resource createProjectResource(Contact contact) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(contact);
		String providerName = contact.refGetPath().getSegment(2)
				.toClassicRepresentation();
		String segmentName = contact.refGetPath().getSegment(4)
				.toClassicRepresentation();
		org.opencrx.kernel.activity1.jmi1.Segment activitySegment = (org.opencrx.kernel.activity1.jmi1.Segment) pm
				.getObjectById(new Path(XRI_ACCOUNT_SEGMENT).getDescendant(
						"provider", providerName, "segment", segmentName));
		try {
			pm.currentTransaction().begin();
			Resource resource = pm.newInstance(Resource.class);
			resource.setName(contact.getFullName());
			resource.setContact(contact);
			activitySegment.addResource(
					org.opencrx.kernel.utils.Utils.getUidAsString(), resource);
			pm.currentTransaction().commit();
			return resource;
		} catch (Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch (Exception ignore) {
			}
		}
		return null;
	}

	/*********************************** Work Record **************************************/

	/**
	 * Get activity work records for given resource.
	 * 
	 * @param resource
	 * @return
	 */
	public static List<WorkAndExpenseRecord> getWorkRecords(Resource resource) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(resource);
		ActivityWorkRecordQuery workRecordQuery = (ActivityWorkRecordQuery) pm
				.newQuery(ActivityWorkRecord.class);
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
	public static WorkAndExpenseRecord createWorkRecord(Resource resource,
			String name, String description, Date startAt,
			Integer durationHours, Integer durationMinutes, Boolean isBillable,
			BigDecimal rate, short rateCurrency, Activity activity) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(resource);
		try {
			pm.currentTransaction().begin();
			ResourceAddWorkRecordParams addWorkRecordParams = Structures
					.create(ResourceAddWorkRecordParams.class,
							Datatypes
									.member(ResourceAddWorkRecordParams.Member.activity,
											activity),
							Datatypes.member(
									ResourceAddWorkRecordParams.Member.name,
									name),
							Datatypes
									.member(ResourceAddWorkRecordParams.Member.description,
											description),
							Datatypes.member(
									ResourceAddWorkRecordParams.Member.startAt,
									startAt),
							Datatypes
									.member(ResourceAddWorkRecordParams.Member.durationHours,
											durationHours),
							Datatypes
									.member(ResourceAddWorkRecordParams.Member.durationMinutes,
											durationMinutes),
							Datatypes
									.member(ResourceAddWorkRecordParams.Member.isBillable,
											isBillable),
							Datatypes.member(
									ResourceAddWorkRecordParams.Member.rate,
									rate),
							Datatypes
									.member(ResourceAddWorkRecordParams.Member.rateCurrency,
											rateCurrency),
							Datatypes
									.member(ResourceAddWorkRecordParams.Member.recordType,
											(short) 0));
			AddWorkAndExpenseRecordResult addWorkRecordResult = resource
					.addWorkRecord(addWorkRecordParams);
			pm.currentTransaction().commit();
			return addWorkRecordResult.getWorkRecord();
		} catch (Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch (Exception ignore) {
			}
		}
		return null;
	}


}
