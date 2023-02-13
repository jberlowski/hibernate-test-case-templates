/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.bugs;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * this test class works as the problem did not occur in hibernate 5.X
 */
public class JoinedEntityHibernate5QueryTest extends BaseCoreFunctionalTestCase {

	@Test
	public void hhh123Query() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Base base = new Base(1L);
		s.persist(base);
		s.flush();
		Base b = s.createQuery("SELECT b FROM Base b", Base.class).getSingleResult();
		tx.rollback();
		s.close();
		assertEquals(base, b);
	}

	@Test
	public void hhh123NativeQuery() throws Exception {
		Session s = openSession();
		Transaction tx = s.beginTransaction();
		Base base = new Base(2L);
		s.persist(base);
		s.flush();
		Base b = s.createNativeQuery("SELECT * FROM BASE", Base.class).getSingleResult();
		tx.rollback();
		s.close();

		assertEquals(base, b);
	}

	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] {
				Base.class,
				Extension.class
		};
	}

	@Override
	protected void configure(Configuration configuration) {
		super.configure( configuration );

		configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.TRUE.toString() );
		configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString() );
	}

	@Entity(name ="Base")
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class Base {
		@Id
		public long id;


		public Base() {
		}

		public Base(long id) {
			this.id = id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Base base = (Base) o;

			return id == base.id;
		}

		@Override
		public int hashCode() {
			return (int) (id ^ (id >>> 32));
		}
	}

	@Entity
	public static class Extension extends Base{

		public Extension() {
		}

		public Extension(long id) {
			this.id = id;
		}
	}


}
