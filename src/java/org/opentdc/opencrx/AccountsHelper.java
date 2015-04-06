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

import java.util.Date;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import org.opencrx.kernel.account1.cci2.AccountQuery;
import org.opencrx.kernel.account1.cci2.GroupQuery;
import org.opencrx.kernel.account1.cci2.MemberQuery;
import org.opencrx.kernel.account1.jmi1.Account;
import org.opencrx.kernel.account1.jmi1.Contact;
import org.opencrx.kernel.account1.jmi1.Group;
import org.opencrx.kernel.account1.jmi1.LegalEntity;
import org.opencrx.kernel.account1.jmi1.Member;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;

/**
 * Sample helpers for address book management.
 *
 */
public abstract class AccountsHelper {

	/**
	 * Create a new contact.
	 * 
	 * @param accountSegment
	 * @param firstName
	 * @param lastName
	 * @return
	 */
	public static Contact createContact(
		PersistenceManager pm,
		org.opencrx.kernel.account1.jmi1.Segment accountSegment,
		String firstName,
		String lastName
	) {
		try {
			pm.currentTransaction().begin();
			Contact contact = pm.newInstance(Contact.class);
			contact.setFirstName(firstName);
			contact.setLastName(lastName);
			accountSegment.addAccount(
				Utils.getUidAsString(),
				contact
			);
			pm.currentTransaction().commit();
			return contact;
		} catch(Exception e) {
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
		}
		return null;
	}

	/**
	 * Create a new legal entity.
	 * 
	 * @param accountSegment
	 * @param name
	 * @return
	 */
	public static LegalEntity createLegalEntity(
		PersistenceManager pm,
		org.opencrx.kernel.account1.jmi1.Segment accountSegment,
		String name
	) {
		try {
			pm.currentTransaction().begin();
			LegalEntity legalEntity = pm.newInstance(LegalEntity.class);
			legalEntity.setName(name);
			accountSegment.addAccount(
				Utils.getUidAsString(),
				legalEntity
			);
			pm.currentTransaction().commit();
			return legalEntity;
		} catch(Exception e) {
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
		}
		return null;
	}

	/**
	 * Get list of address books, i.e. groups with account type = 100.
	 * 
	 * @param accountSegment
	 * @return
	 */
	public static final List<Group> getAddressBooks(
		org.opencrx.kernel.account1.jmi1.Segment accountSegment
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(accountSegment);
		GroupQuery addressBookQuery = (GroupQuery)pm.newQuery(Group.class);
		addressBookQuery.forAllDisabled().isFalse();
		addressBookQuery.thereExistsAccountType().equalTo(ACCOUNT_TYPE_ADDRESS_BOOK);
		return accountSegment.getAccount(addressBookQuery);
	}
	
	/**
	 * Create a new address book.
	 * 
	 * @param accountSegment
	 * @param name
	 * @param description
	 * @return
	 */
	public static Group createAddressBook(
		PersistenceManager pm,
		org.opencrx.kernel.account1.jmi1.Segment accountSegment,
		String name,
		String description
	) {
		try {
			pm.currentTransaction().begin();
			Group addressBook = pm.newInstance(Group.class);
			addressBook.setName(name);
			addressBook.setDescription(description);
			accountSegment.addAccount(
				Utils.getUidAsString(),
				addressBook
			);
			pm.currentTransaction().commit();
			return addressBook;
		} catch(Exception e) {
			new ServiceException(e).log();
			try {
				pm.currentTransaction().rollback();
			} catch(Exception ignore) {}
		}
		return null;
	}

	/**
	 * Get active members for given address book.
	 * 
	 * @param addressBook
	 * @return
	 */
	public static List<Member> getAddressBookMembers(
		Group addressBook
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(addressBook);
		MemberQuery memberQuery = (MemberQuery)pm.newQuery(Member.class);
		memberQuery.forAllDisabled().isFalse();
		memberQuery.thereExistsAccount().forAllDisabled().isFalse();
		return addressBook.getMember(memberQuery);
	}
	
	/**
	 * Get accounts for given address book.
	 * 
	 * @param addressBook
	 * @return
	 */
	public static List<Account> getAddressBookAccounts(
		Group addressBook
	) {
		PersistenceManager pm = JDOHelper.getPersistenceManager(addressBook);
		String providerName = addressBook.refGetPath().getSegment(2).toClassicRepresentation();
		String segmentName = addressBook.refGetPath().getSegment(4).toClassicRepresentation();
		org.opencrx.kernel.account1.jmi1.Segment accountSegment = 
			(org.opencrx.kernel.account1.jmi1.Segment)pm.getObjectById(
				new Path("xri://openmdx*org.opencrx.kernel.account1").getDescendant("provider", providerName, "segment", segmentName)
			);
		AccountQuery accountQuery = (AccountQuery)pm.newQuery(Account.class);
		accountQuery.forAllDisabled().isFalse();
		accountQuery.thereExistsAccountMembership().forAllDisabled().isFalse();
		accountQuery.thereExistsAccountMembership().thereExistsAccountFrom().equalTo(addressBook);
		return accountSegment.getAccount(accountQuery);
	}
	
	/**
	 * Add account as member to given address book.
	 * 
	 * @param addressBook
	 * @param account
	 * @param validFrom
	 * @param validTo
	 * @param roles
	 * @return
	 */
	public static Member addAccountToAddressBook(
		PersistenceManager pm,
		Group addressBook,
		Account account,
		Date validFrom,
		Date validTo,
		List<Short> roles
	) {
		try {
			pm.currentTransaction().begin();
			Member member = pm.newInstance(Member.class);
			member.setName(account.getFullName());
			member.setAccount(account);
			member.setValidFrom(validFrom);
			member.setValidTo(validTo);
			if(roles != null) {
				member.getMemberRole().addAll(roles);
			}
			addressBook.addMember(
				Utils.getUidAsString(),
				member
			);
			pm.currentTransaction().commit();
			return member;
		} catch(Exception e) {
			try {
				pm.currentTransaction().rollback();
			} catch(Exception igore) {}
		}
		return null;
	}
	
	public static final short ACCOUNT_TYPE_ADDRESS_BOOK = 100;

}
