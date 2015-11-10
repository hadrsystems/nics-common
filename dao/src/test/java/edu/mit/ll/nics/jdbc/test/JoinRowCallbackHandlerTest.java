/**
 * Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.mit.ll.nics.jdbc.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import javax.sql.DataSource;

import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.jdbc.test.mapper.*;
import edu.mit.ll.nics.jdbc.test.model.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class JoinRowCallbackHandlerTest {
    
    @Autowired
    private DataSource datasource;

    private NamedParameterJdbcTemplate template;
    private JoinRowCallbackHandler<ParentObject> handler;

    @Before
    public void setUp() {
        template = new NamedParameterJdbcTemplate(datasource);
    }

    @Test
    public void testMapSingleRowWithoutJoin() {
        handler = getHandlerWith();
        template.query("select * from parent_obj where parent_id = :id", new MapSqlParameterSource("id", 1), handler);
        ParentObject parent = handler.getSingleResult();
        assertTrue(parent.getId() == 1);
        assertTrue(parent.getName().equals("Parent: has single child 1"));
        assertTrue(parent.getSingleChild() == null);
        assertTrue(parent.getMultipleChildren() == null);
        assertTrue(parent.getDeepChild() == null);
    }

    @Test
    public void testMapMultipleRowsWithoutJoin() {
        handler = getHandlerWith();
        template.query("select * from parent_obj where parent_id > :id", new MapSqlParameterSource("id", 0), handler);
        List<ParentObject> parents = handler.getResults();
        
        // Check first parent
        ParentObject parent1 = parents.get(0);
        assertTrue(parent1.getId() == 1);
        assertTrue(parent1.getName().equals("Parent: has single child 1"));
        assertTrue(parent1.getSingleChild() == null);
        assertTrue(parent1.getMultipleChildren() == null);
        assertTrue(parent1.getDeepChild() == null);

        // Check second
        ParentObject parent2 = parents.get(1);
        assertTrue(parent2.getId() == 2);
        assertTrue(parent2.getName().equals("Parent: has multiple children 1 & 2"));
        assertTrue(parent2.getSingleChild() == null);
        assertTrue(parent2.getMultipleChildren() == null);
        assertTrue(parent2.getDeepChild() == null);
    }

    @Test
    public void testMapSingleRowWithSingleJoin() {
        handler = getHandlerWith(new SingleChildRowMapper());
        template.query("select * from parent_obj join single_child using (single_child_id) where parent_id = :id", new MapSqlParameterSource("id", 1), handler);
        ParentObject parent = handler.getSingleResult();
        assertTrue(parent.getId() == 1);
        assertTrue(parent.getName().equals("Parent: has single child 1"));
        SingleChild child = parent.getSingleChild();
        assertTrue(child != null);
        assertTrue(child.getId() == 1);
        assertTrue(child.getName().equals("Single child 1"));
        assertTrue(parent.getMultipleChildren() == null);
        assertTrue(parent.getDeepChild() == null);
    }

    @Test
    public void testMapMultipleRowsWithSingleJoin() {
        handler = getHandlerWith(new SingleChildRowMapper());
        template.query("select * from parent_obj join single_child using(single_child_id) where parent_id in (:id1, :id2)", 
            new MapSqlParameterSource("id1", 1).addValue("id2", 4), handler);
        List<ParentObject> parents = handler.getResults();
        
        // Check first parent
        ParentObject parent1 = parents.get(0);
        assertTrue(parent1.getId() == 1);
        assertTrue(parent1.getName().equals("Parent: has single child 1"));
        SingleChild child1 = parent1.getSingleChild();
        assertTrue(child1 != null);
        assertTrue(child1.getId() == 1);
        assertTrue(child1.getName().equals("Single child 1"));
        assertTrue(parent1.getMultipleChildren() == null);
        assertTrue(parent1.getDeepChild() == null);

        // Check second
        ParentObject parent2 = parents.get(1);
        assertTrue(parent2.getId() == 4);
        assertTrue(parent2.getName().equals("Parent: has single child 2"));
        SingleChild child2 = parent2.getSingleChild();
        assertTrue(child2 != null);
        assertTrue(child2.getId() == 2);
        assertTrue(child2.getName().equals("Single child 2"));
        assertTrue(parent2.getMultipleChildren() == null);
        assertTrue(parent2.getDeepChild() == null);
    }

    @Test
    public void testMapSingleRowWithMultipleJoins() {
        handler = getHandlerWith(new MultipleChildRowMapper());
        template.query("select * from parent_obj join parent_obj_multiple_child using(parent_id) join multiple_child using (multiple_child_id) where parent_id = :id", new MapSqlParameterSource("id", 2), handler);
        ParentObject parent = handler.getSingleResult();
        assertTrue(parent.getId() == 2);
        assertTrue(parent.getName().equals("Parent: has multiple children 1 & 2"));
        assertTrue(parent.getSingleChild() == null);
        List<MultipleChild> children = parent.getMultipleChildren();
        assertTrue(children != null);
        assertTrue(children.size() == 2);
        assertTrue(children.get(0).getId() == 1);
        assertTrue(children.get(0).getName().equals("Multiple child 1"));
        assertTrue(children.get(1).getId() == 2);
        assertTrue(children.get(1).getName().equals("Multiple child 2"));
        assertTrue(parent.getDeepChild() == null);
    }

    @Test
    public void testMapMultipleRowsWithMultipleJoins() {
        handler = getHandlerWith(new MultipleChildRowMapper());
        template.query("select * from parent_obj join parent_obj_multiple_child using(parent_id) join multiple_child using (multiple_child_id)", new MapSqlParameterSource(), handler);
        List<ParentObject> parents = handler.getResults();

        // Check first parent
        ParentObject parent1 = parents.get(0);
        assertTrue(parent1.getId() == 2);
        assertTrue(parent1.getName().equals("Parent: has multiple children 1 & 2"));
        assertTrue(parent1.getSingleChild() == null);
        List<MultipleChild> children1 = parent1.getMultipleChildren();
        assertTrue(children1 != null);
        assertTrue(children1.size() == 2);
        assertTrue(children1.get(0).getId() == 1);
        assertTrue(children1.get(0).getName().equals("Multiple child 1"));
        assertTrue(children1.get(1).getId() == 2);
        assertTrue(children1.get(1).getName().equals("Multiple child 2"));
        assertTrue(parent1.getDeepChild() == null);

        // Check second parent
        ParentObject parent2 = parents.get(1);
        assertTrue(parent2.getId() == 5);
        assertTrue(parent2.getName().equals("Parent: has mulitple children 2, 3 & 4"));
        assertTrue(parent2.getSingleChild() == null);
        List<MultipleChild> children2 = parent2.getMultipleChildren();
        assertTrue(children2 != null);
        assertTrue(children2.size() == 3);
        assertTrue(children2.get(0).getId() == 2);
        assertTrue(children2.get(0).getName().equals("Multiple child 2"));
        assertTrue(children2.get(1).getId() == 3);
        assertTrue(children2.get(1).getName().equals("Multiple child 3"));
        assertTrue(children2.get(2).getId() == 4);
        assertTrue(children2.get(2).getName().equals("Multiple child 4"));
        assertTrue(parent2.getDeepChild() == null);
    }

        @Test
    public void testMapDeepJoin() {
        handler = getHandlerWith(new DeepChildRowMapper().attachAdditionalMapper(new SingleChildRowMapper()));
        String sql = "select * from parent_obj join deep_child " + 
            "using(deep_child_id) join single_child on deep_child.single_child_id " + 
            "= single_child.single_child_id";
        template.query(sql, new MapSqlParameterSource("id", 3), handler);
        List<ParentObject> parents = handler.getResults();

        // Check first parent
        ParentObject parent1 = parents.get(0);
        assertTrue(parent1.getId() == 3);
        assertTrue(parent1.getName().equals("Parent: has deep child 1"));
        assertTrue(parent1.getSingleChild() == null);
        DeepChild child1 = parent1.getDeepChild();
        assertTrue(child1 != null);
        assertTrue(child1.getId() == 1);
        assertTrue(child1.getName().equals("Deep child 1"));
        SingleChild nestedChild1 = child1.getChild();
        assertTrue(nestedChild1 != null);
        assertTrue(nestedChild1.getId() == 1);
        assertTrue(nestedChild1.getName().equals("Single child 1"));
        assertTrue(parent1.getMultipleChildren() == null);

        // Check second parent
        ParentObject parent2 = parents.get(1);
        assertTrue(parent2.getId() == 6);
        assertTrue(parent2.getName().equals("Parent: has deep child 2"));
        assertTrue(parent2.getSingleChild() == null);
        DeepChild child2 = parent2.getDeepChild();
        assertTrue(child2 != null);
        assertTrue(child2.getId() == 2);
        assertTrue(child2.getName().equals("Deep child 2"));
        SingleChild nestedChild2 = child2.getChild();
        assertTrue(nestedChild2 != null);
        assertTrue(nestedChild2.getId() == 3);
        assertTrue(nestedChild2.getName().equals("Single child 3"));
        assertTrue(parent2.getMultipleChildren() == null);
    }

    @Test
    public void testMapAllJoin() {
        handler = getHandlerWith(new SingleChildRowMapper(), new DeepChildRowMapper(), new MultipleChildRowMapper());
        String sql = "select * from parent_obj left join deep_child " + 
            "using(deep_child_id) left join single_child on " + 
            "parent_obj.single_child_id = single_child.single_child_id left " + 
            "join parent_obj_multiple_child using(parent_id) left join " + 
            "multiple_child using (multiple_child_id) where parent_id = :id";
        template.query(sql, new MapSqlParameterSource("id", 7), handler);

        ParentObject parent = handler.getSingleResult();
        assertTrue(parent.getId() == 7);
        assertTrue(parent.getName().equals("Parent: has single child 3, multiple children 4 & 5, deep child 1"));
        SingleChild child = parent.getSingleChild();
        assertTrue(child != null);
        assertTrue(child.getId() == 3);
        assertTrue(child.getName().equals("Single child 3"));
        List<MultipleChild> children = parent.getMultipleChildren();
        assertTrue(children != null);
        assertTrue(children.size() == 2);
        assertTrue(children.get(0).getId() == 4);
        assertTrue(children.get(0).getName().equals("Multiple child 4"));
        assertTrue(children.get(1).getId() == 5);
        assertTrue(children.get(1).getName().equals("Multiple child 5"));
        DeepChild deepChild = parent.getDeepChild();
        assertTrue(deepChild != null);
        assertTrue(deepChild.getId() == 1);
        assertTrue(deepChild.getName().equals("Deep child 1"));


    }

    private JoinRowCallbackHandler<ParentObject> getHandlerWith(JoinRowMapper... mappers) {
        return new JoinRowCallbackHandler(new ParentObjectRowMapper(), mappers);
    }
}
