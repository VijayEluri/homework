package cn.edu.sjtu.acm.jdbctaste.dao.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import cn.edu.sjtu.acm.jdbctaste.dao.JokeDao;
import cn.edu.sjtu.acm.jdbctaste.dao.PersonDao;
import cn.edu.sjtu.acm.jdbctaste.entity.Joke;
import cn.edu.sjtu.acm.jdbctaste.entity.Person;

public class SqlitePersonDao implements PersonDao {

	public static final int IDX_ID = 1, IDX_NAME = 2, IDX_EMAIL = 3;

	private final Connection conn;

	public SqlitePersonDao(Connection conn) {
		this.conn = conn;
	}

	@Override
	public int insertPerson(Person person) {

		int ret = -1;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"insert into person (name, email) values (?,?);",
					Statement.RETURN_GENERATED_KEYS);
			stat.setString(1, person.getName());
			stat.setString(2, person.getEmail());

			stat.executeUpdate();
			
			ResultSet rs = stat.getGeneratedKeys();
			
			if (rs.next()) {
				int id = rs.getInt(1);
				person.setId(id);
				ret = id;
			}

			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			ret = -1;
		}

		return ret;
	}

	@Override
	public boolean deletePerson(Person person) {
		if (person.getId() == null || person.getId() == -1) return false;
		boolean ret = false;
		try {
			PreparedStatement stat = conn.prepareStatement(
					"delete from person where id=?;");
			stat.setInt(1, person.getId());
			if (stat.executeUpdate() != Statement.EXECUTE_FAILED) {
				JokeDao jokeDao = SqliteDaoFactory.getInstance().getJokeDao();
				List<Joke> list = jokeDao.findJokesOfPerson(person);
				for (Joke joke : list) {
					/*System.out.println(joke.getBody() + " " +
								joke.getId());*/
					jokeDao.deleteJoke(joke);
				}
				
				PreparedStatement stat2 = conn.prepareStatement(
						"delete from comment where commentator=?");
				stat2.setInt(1, person.getId());
				stat2.executeUpdate();
				stat2.close();
				
				person.setId(-1);
				ret = true;
			}
			
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public boolean updatePerson(Person person) {
		if (person.getId() == null || person.getId() == -1) return false;
		if (person.getName() == null || person.getEmail() == null) return false;
		boolean ret = false;
		try {
			PreparedStatement stat = conn.prepareStatement(
					"update person SET name=?, email=? where id=?;");
			stat.setString(1, person.getName());
			stat.setString(2, person.getEmail());
			stat.setInt(3, person.getId());
			ret = (stat.executeUpdate() != Statement.EXECUTE_FAILED);
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public Person findPersonByEmail(String email) {
		Person ret = null;
		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from person where email=?;");
			stat.setString(1, email);
			ResultSet rs = stat.executeQuery();
			if (rs.next())
				ret = new Person(rs.getInt(IDX_ID), 
						rs.getString(IDX_NAME), 
						rs.getString(IDX_EMAIL));
			
			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public int getNumOfJokes(Person person) {
		if (person.getId() == null || person.getId() == -1) return 0;
		int ret = 0;
		try {
			PreparedStatement stat = conn.prepareStatement(
					"select count(id) from joke where speaker=?");
			stat.setInt(1, person.getId());
			ResultSet result = stat.executeQuery();
			//if (result == null || !result.next()) return 0;
			//return result.getInt(1);
			if (result != null && result.next())
				ret = result.getInt(1);
			
			result.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public List<Person> getAllPerson() {
		List<Person> ret = new LinkedList<Person>();

		Statement stat;
		try {
			stat = conn.createStatement();

			stat.execute("select * from person;");
			ResultSet result = stat.getResultSet();

			while (result.next()) {
				ret.add(new Person(result.getInt(IDX_ID), result
						.getString(IDX_NAME), result.getString(IDX_EMAIL)));
			}
			result.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}

	@Override
	public Person findPersonById(int id) {
		Person ret = null;
		try {
			PreparedStatement stat = conn.prepareStatement(
					"select * from person where id=?;");
			stat.setInt(1, id);
			ResultSet rs = stat.executeQuery();
			if (rs.next())
				ret = new Person(rs.getInt(IDX_ID), 
						rs.getString(IDX_NAME), 
						rs.getString(IDX_EMAIL));
			
			rs.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

}
