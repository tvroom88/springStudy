package com.example.boardproejct.repository;

import com.example.boardproejct.entity.Article;
import com.example.boardproejct.entity.BoardUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ArticleRepositoryImpl implements ArticleRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ArticleRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Create
    @Override
    public void save(Article article) {
        String updateSQL = "INSERT INTO article (title, content, create_date, modify_date, category, author_id) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement pstmt = conn.prepareStatement(
                    updateSQL, new String[]{"id"});
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getContent());
            pstmt.setObject(3, article.getCreateDate());
            pstmt.setObject(4, article.getModifyDate());
            pstmt.setObject(5, article.getCategory());
            pstmt.setLong(6, article.getAuthor().getId());
            return pstmt;
        }, keyHolder);
    }

    public void save(BoardUser boardUser) {
        String updateSQL = "INSERT INTO board_user (email, password, username) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement pstmt = conn.prepareStatement(
                    updateSQL, new String[]{"id"});
            pstmt.setString(1, boardUser.getEmail());
            pstmt.setString(2, boardUser.getPassword());
            pstmt.setString(3, boardUser.getUsername());
            return pstmt;
        }, keyHolder);
    }

    @Override
    public BoardUser findByusername(String username) {
        List<BoardUser> list = jdbcTemplate.query("SELECT * FROM board_user WHERE USERNAME = ?", boardUserRowMapper(), username);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    // ------ Read ------
    // 모든 Article 가져오는 부분
    @Override
    public List<Article> findAll() {
        return jdbcTemplate.query("SELECT * FROM ARTICLE", memberRowMapper());
    }

    //게시판별 게시글 가져오기
    @Override
    public List<Article> findArticleByCategory(int category) {
        return jdbcTemplate.query("SELECT * FROM ARTICLE WHERE CATEGORY = ?", memberRowMapper(), category);
    }

    @Override
    public Article findArticleById(int id) {
        List<Article> list = jdbcTemplate.query("SELECT * FROM ARTICLE WHERE ID = ?", memberRowMapper(), id);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    // id별로 SiteUser 얻는 메소드
    public BoardUser userFindById(Long id) {
        List<BoardUser> result = jdbcTemplate.query("SELECT * FROM board_user WHERE ID = ?", boardUserRowMapper(), id);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    //Search
    @Override
    public List<Article> findArticleByKeyword(String keyword){
        String searchSQL = "SELECT * FROM article WHERE title LIKE ?";
        keyword = String.format("%%%s%%", keyword);
        return jdbcTemplate.query(searchSQL, memberRowMapper(), keyword);
    }

    //Update
    @Override
    public void updateArticle(Article article) {
        String updateSQL = "UPDATE ARTICLE SET title = ?, content = ?, modify_date = ? WHERE id = ?";
        jdbcTemplate.update(updateSQL, article.getTitle(), article.getContent(), article.getModifyDate(), article.getId());
    }

    @Override
    public void deleteArticle(int id) {
        String deleteSQL = "DELETE FROM ARTICLE WHERE id = ?";
        jdbcTemplate.update(deleteSQL, id);
    }


    // Article Mapper
    private RowMapper<Article> memberRowMapper() {
        return (rs, rowNum) -> {
            Article member = new Article();
            member.setId(rs.getInt("id"));
            member.setTitle(rs.getString("title"));
            member.setContent(rs.getString("content"));
            member.setCreateDate(rs.getObject("create_date", LocalDateTime.class));
            member.setAuthor(userFindById(rs.getLong("author_id")));
            member.setModifyDate(rs.getObject("modify_date", LocalDateTime.class));
            member.setCategory(rs.getInt("category"));
            return member;
        };
    }

    //BoardUser Mapper
    private RowMapper<BoardUser> boardUserRowMapper() {
        return (rs, rowNum) -> {
            BoardUser user = new BoardUser();
            user.setId(rs.getLong("id"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setUsername(rs.getString("username"));
            return user;
        };
    }

}
