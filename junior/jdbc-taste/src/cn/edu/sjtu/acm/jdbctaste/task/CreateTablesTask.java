package cn.edu.sjtu.acm.jdbctaste.task;

import cn.edu.sjtu.acm.jdbctaste.Taste;
import cn.edu.sjtu.acm.jdbctaste.TasteTask;

/**
 * This task is to create tree tables in sqlite, called person, joke and comment
 * Caution: Don't use reference, we first use naive way to do the same thing.
 * Notice: In sqlite, database used is infereced from your connection url, so no "create database taste;" or "use taste"
 * @author furtherlee
 */
public class CreateTablesTask implements TasteTask {

	private Taste taste;

	public CreateTablesTask(Taste taste) {
		this.taste = taste;
	}

	private static final String PERSON_SCHEMA = 
			"create table person(" +
			"id integer primary key autoincrement," +
			"name varchar(200) not null," +
			"email varchar(200) not null);";

	private static final String JOKE_SCHEMA = 
			"create table joke(" +
			"id integer primary key autoincrement," + 
			"body varchar (200)," +
			"speaker integer not null," +
			"posttime timestamp default current_timestamp," + 
			"zan integer);";

	private static final String COMMENT_SCHEMA = 
			"create table comment(" +
			"id integer primary key autoincrement," +
			"body varchar (200)," +
			"joke integer not null," +
			"commentator integer not null," +
			"posttime timestamp default current_timestamp);";

	@Override
	public boolean doit() {
		try {
			taste.getDaoFactory().getConn().createStatement().execute(PERSON_SCHEMA);
			taste.getDaoFactory().getConn().createStatement().execute(JOKE_SCHEMA);
			taste.getDaoFactory().getConn().createStatement().execute(COMMENT_SCHEMA);
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
