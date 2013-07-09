package cn.edu.sjtu.acm.jdbctaste.dao.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import cn.edu.sjtu.acm.jdbctaste.dao.CommentDao;
import cn.edu.sjtu.acm.jdbctaste.dao.JokeDao;
import cn.edu.sjtu.acm.jdbctaste.dao.PersonDao;
import cn.edu.sjtu.acm.jdbctaste.entity.Comment;
import cn.edu.sjtu.acm.jdbctaste.entity.Joke;
import cn.edu.sjtu.acm.jdbctaste.entity.Person;

public class SqliteCommentDao implements CommentDao {

	private final Connection conn;
	
	public static final int IDX_ID = 1, IDX_BODY = 2, IDX_JOKE = 3, IDX_COMMENTATOR = 4, IDX_POST_TIME = 5;
	
	public SqliteCommentDao(Connection conn) {
		this.conn = conn;
	}

	@Override
	public int insertComment(Comment comment) {
		int ret = -1;

		try {
			PreparedStatement stat = conn.prepareStatement(
					"insert into comment (commentator, joke, body, posttime) " +
					"values (?,?,?,?);",
					Statement.RETURN_GENERATED_KEYS);
			stat.setInt(1, comment.getCommentator().getId());
			stat.setInt(2, comment.getJoke().getId());
			stat.setString(3, comment.getBody());
			stat.setTimestamp(4, comment.getPostTime());

			stat.executeUpdate();
			
			ResultSet rs = stat.getGeneratedKeys();
			
			if (rs.next()) {
				int id = rs.getInt(1);
				comment.setId(id);
				ret = id;
				//System.out.println("Comment #" + id + " inserted!");
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
	public boolean deleteComment(Comment comment) {
		if (comment.getId() == null || comment.getId() == -1) return false;
		boolean ret = false;
		try {
			PreparedStatement stat = conn.prepareStatement(
					"delete from comment where id=?;");
			stat.setInt(1, comment.getId());
			if (stat.executeUpdate() != Statement.EXECUTE_FAILED) {
				comment.setId(-1);
				ret = true;
			}
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public boolean updateComment(Comment comment) {
		if (comment.getId() == null || comment.getId() == -1) return false;
		boolean ret = false;
		try {
			PreparedStatement stat = conn.prepareStatement(
					"update comment set commentator=?, joke=?, body=?, posttime=? " +
					"where id=?;");
			stat.setInt(1, comment.getCommentator().getId());
			stat.setInt(2, comment.getJoke().getId());
			stat.setString(3, comment.getBody());
			stat.setTimestamp(4, comment.getPostTime());
			stat.setInt(5, comment.getId());
			if (stat.executeUpdate() != Statement.EXECUTE_FAILED) 
				ret = true;
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	public List<Comment> findCommentsOfPerson(Person person) {
		List<Comment> ret = new LinkedList<Comment>();

		PreparedStatement stat;
		try {
			stat = conn.prepareStatement("select * from comment where commentator=?;");
			stat.setInt(1, person.getId());
			ResultSet result = stat.executeQuery();

			while (result.next()) {
				JokeDao jokeDao = SqliteDaoFactory.getInstance().getJokeDao();
				Joke joke = jokeDao.findJokeById(result.getInt(IDX_JOKE));
				ret.add(new Comment(result.getInt(IDX_ID), joke, person,
						result.getString(IDX_BODY), 
						result.getTimestamp(IDX_POST_TIME)));
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
	public List<Comment> findCommentsReceived(Person person) {
		List<Comment> ret = new LinkedList<Comment>();
		JokeDao jokeDao = SqliteDaoFactory.getInstance().getJokeDao();
		List<Joke> list = jokeDao.findJokesOfPerson(person);
		for (Joke joke : list)
			ret.addAll(findCommentsOfJoke(joke));
		return ret;
	}

	@Override
	public List<Comment> findCommentsOfJoke(Joke joke) {
		List<Comment> ret = new LinkedList<Comment>();

		PreparedStatement stat;
		try {
			stat = conn.prepareStatement("select * from comment where joke=?;");
			stat.setInt(1, joke.getId());
			ResultSet result = stat.executeQuery();

			while (result.next()) {				
				PersonDao personDao = SqliteDaoFactory.getInstance().getPersonDao();
				Person commentator = personDao.findPersonById(result.getInt(IDX_COMMENTATOR));
				ret.add(new Comment(result.getInt(IDX_ID), joke, commentator,
						result.getString(IDX_BODY), 
						result.getTimestamp(IDX_POST_TIME)));
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
	public List<Comment> getAllComments() {
		List<Comment> ret = new LinkedList<Comment>();

		Statement stat;
		try {
			stat = conn.createStatement();

			stat.execute("select * from comment;");
			ResultSet result = stat.getResultSet();

			while (result.next()) {
				JokeDao jokeDao = SqliteDaoFactory.getInstance().getJokeDao();
				Joke joke = jokeDao.findJokeById(result.getInt(IDX_JOKE));
				
				PersonDao personDao = SqliteDaoFactory.getInstance().getPersonDao();
				Person commentator = personDao.findPersonById(result.getInt(IDX_COMMENTATOR));
				ret.add(new Comment(result.getInt(IDX_ID), joke, commentator,
						result.getString(IDX_BODY), 
						result.getTimestamp(IDX_POST_TIME)));
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
	public Comment findCommentById(int id) {
		Comment ret = null;

		PreparedStatement stat;
		try {
			stat = conn.prepareStatement("select * from comment where id=?;");
			stat.setInt(1, id);
			ResultSet result = stat.executeQuery();

			if (result.next()) {
				JokeDao jokeDao = SqliteDaoFactory.getInstance().getJokeDao();
				Joke joke = jokeDao.findJokeById(result.getInt(IDX_JOKE));
				
				PersonDao personDao = SqliteDaoFactory.getInstance().getPersonDao();
				Person commentator = personDao.findPersonById(result.getInt(IDX_COMMENTATOR));
				
				ret = new Comment(result.getInt(IDX_ID), joke, commentator,
						result.getString(IDX_BODY), 
						result.getTimestamp(IDX_POST_TIME));
			}
			result.close();
			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}

}
