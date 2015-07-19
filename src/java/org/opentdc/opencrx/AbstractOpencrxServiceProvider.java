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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;

public class AbstractOpencrxServiceProvider {

	private static Map<String,PersistenceManagerFactory> pmfs = new ConcurrentHashMap<String,PersistenceManagerFactory>();
	
	// instance variables
	private final String providerName;
	private final String segmentName;
	private final PersistenceManager pm;
	
	public AbstractOpencrxServiceProvider(
		ServletContext context,
		String prefix
	) throws ServiceException, NamingException {
		String url = context.getInitParameter("opencrx.url");
		String userName = context.getInitParameter("opencrx.userName");
		String password = context.getInitParameter("opencrx.password");
		String mimeType = context.getInitParameter("opencrx.mimeType");
		String key = url + ":" + userName + ":" + password + ":" + mimeType;
		PersistenceManagerFactory pmf = pmfs.get(key);
		if(pmf == null) {
			pmfs.put(
				key,
				pmf = org.opencrx.kernel.utils.Utils.getPersistenceManagerFactoryProxy(
					url, 
					userName,
					password, 
					mimeType
				)
			);
		}
		this.providerName = context.getInitParameter("opencrx.providerName");
		this.segmentName = context.getInitParameter("opencrx.segmentName");
		this.pm = pmf.getPersistenceManager(userName, null);
	}
	
	protected PersistenceManager getPersistenceManager(
	) {
		return this.pm;
	}
	
	protected String getProviderName(
	) {
		return this.providerName;
	}
	
	protected String getSegmentName(
	) {
		return this.segmentName;
	}
	
	protected org.opencrx.kernel.activity1.jmi1.Segment getActivitySegment(
	) {
		return (org.opencrx.kernel.activity1.jmi1.Segment)this.getPersistenceManager().getObjectById(
			new Path("xri://@openmdx*org.opencrx.kernel.activity1").getDescendant(
				"provider", this.getProviderName(), "segment", this.getSegmentName()
			)
		);
	}

	protected org.opencrx.kernel.account1.jmi1.Segment getAccountSegment(
	) {
		return (org.opencrx.kernel.account1.jmi1.Segment)this.getPersistenceManager().getObjectById(
			new Path("xri://@openmdx*org.opencrx.kernel.account1").getDescendant(
				"provider", this.getProviderName(), "segment", this.getSegmentName()
			)
		);
	}

	protected org.opencrx.kernel.code1.jmi1.Segment getCodeSegment(
	) {
		return (org.opencrx.kernel.code1.jmi1.Segment)this.getPersistenceManager().getObjectById(
			new Path("xri://@openmdx*org.opencrx.kernel.code1").getDescendant(
				"provider", this.getProviderName(), "segment", this.getSegmentName()
			)
		);
	}
	
}
