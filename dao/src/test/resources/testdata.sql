--
-- Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
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

INSERT INTO single_child VALUES
    (1, 'Single child 1'), 
    (2, 'Single child 2'), 
    (3, 'Single child 3');

INSERT INTO deep_child VALUES
    (1, 'Deep child 1', 1),
    (2, 'Deep child 2', 3);

INSERT INTO parent_obj VALUES 
    (1, 'Parent: has single child 1', 1, null),
    (2, 'Parent: has multiple children 1 & 2', null, null),
    (3, 'Parent: has deep child 1', null, 1),
    (4, 'Parent: has single child 2', 2, null),
    (5, 'Parent: has mulitple children 2, 3 & 4', null, null),
    (6, 'Parent: has deep child 2', null, 2),
    (7, 'Parent: has single child 3, multiple children 4 & 5, deep child 1', 3, 1);

INSERT INTO multiple_child VALUES
    (1, 'Multiple child 1'),
    (2, 'Multiple child 2'),
    (3, 'Multiple child 3'),
    (4, 'Multiple child 4'),
    (5, 'Multiple child 5');

INSERT INTO parent_obj_multiple_child VALUES
    (2, 1),
    (2, 2),
    (5, 2),
    (5, 3),
    (5, 4),
    (7, 4),
    (7, 5);
