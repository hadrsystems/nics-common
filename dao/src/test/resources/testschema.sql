--
-- Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions are met:
--
-- 1. Redistributions of source code must retain the above copyright notice, this
-- list of conditions and the following disclaimer.
--
-- 2. Redistributions in binary form must reproduce the above copyright notice,
-- this list of conditions and the following disclaimer in the documentation
-- and/or other materials provided with the distribution.
--
-- 3. Neither the name of the copyright holder nor the names of its contributors
-- may be used to endorse or promote products derived from this software without
-- specific prior written permission.
--
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
-- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
-- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
-- DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
-- FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
-- DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
-- SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
-- CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
-- OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
-- OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--

CREATE TABLE single_child
(
    single_child_id integer NOT NULL PRIMARY KEY,
    name varchar (128) NOT NULL
);

CREATE TABLE deep_child
(
    deep_child_id integer NOT NULL PRIMARY KEY,
    name varchar (128) NOT NULL,
    single_child_id integer NOT NULL,
    CONSTRAINT fk_deep_child_single_child_id FOREIGN KEY(single_child_id) REFERENCES single_child (single_child_id)
);

CREATE TABLE parent_obj
(
    parent_id integer NOT NULL PRIMARY KEY,
    name varchar (128) NOT NULL,
    single_child_id integer,
    deep_child_id integer,
    CONSTRAINT fk_parent_obj_single_child_id FOREIGN KEY(single_child_id) REFERENCES single_child (single_child_id),
    CONSTRAINT fk_parent_obj_deep_child_id FOREIGN KEY(deep_child_id) REFERENCES deep_child (deep_child_id)
);

CREATE TABLE multiple_child
(
    multiple_child_id integer NOT NULL PRIMARY KEY,
    name varchar (128) NOT NULL
);

CREATE TABLE parent_obj_multiple_child
(
    parent_id integer NOT NULL,
    multiple_child_id integer NOT NULL,
    CONSTRAINT fk_parent_obj_multiple_child_parent_id FOREIGN KEY(parent_id) REFERENCES parent_obj (parent_id),
    CONSTRAINT fk_parent_obj_multiple_child_multiple_child_id FOREIGN KEY(multiple_child_id) REFERENCES multiple_child (multiple_child_id)
)
