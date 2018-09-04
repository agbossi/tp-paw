package ar.edu.itba.pawddit.persistence;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;


import ar.edu.itba.pawddit.model.Group;
import ar.edu.itba.pawddit.model.User;

@Repository
public class GroupJdbcDao implements GroupDao {
	
	private final JdbcTemplate jdbcTemplate;
	private final SimpleJdbcInsert jdbcInsert;
	private final static RowMapper<Group> ROW_MAPPER = (rs, rowNum) ->
		new Group(rs.getString("name"), rs.getTimestamp("creationdate"), rs.getString("description"), new User(rs.getString("username"), rs.getString("email"), rs.getString("password"), rs.getInt("score"), rs.getInt("userid")));
		
	
	@Autowired
	public GroupJdbcDao(final DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
		jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("groups");
	}
	
	@Override
	public Optional<Group> findByName(final String name){
		return jdbcTemplate.query("SELECT * FROM groups JOIN users ON groups.owner = users.userid WHERE name = ?", ROW_MAPPER, name).stream().findFirst();
	}
	
	@Override
	public Group create(final String name, final Timestamp date, final String description, final User owner) {
		final Map<String, Object> args = new HashMap<>();
		args.put("name", name);
		args.put("creationdate", date);
		args.put("description", description);
		args.put("owner", owner.getUserid());
		jdbcInsert.execute(args);
		return new Group(name, date, description, owner); 
	}
	
}
