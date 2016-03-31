/**
 * Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.nics.nicsdao;

import java.util.List;

import edu.mit.ll.nics.common.entity.OrgFolder;
import edu.mit.ll.nics.common.entity.datalayer.Folder;
import edu.mit.ll.nics.common.entity.datalayer.Rootfolder;

public interface FolderDAO extends BaseDAO {
	public List<Folder> getOrderedFolders(String folderid, int workspaceid);
    public Rootfolder getRootFolder(String name, int workspaceid);
    public Folder getFolder(String folderid);
    public void updateParentFolderId(String folderId, String parentFolderId);
    public void updateDataLayerFolderId(String datalayerId, String folderId);
    public String getFolderId(String foldername, int workspaceId);
    public int getNextFolderIndex(String folderid);
    public OrgFolder getFolderOwner(String folderid);
    public Folder getFolderByName(String foldername, int workspaceId);
    public Folder createFolder(Folder folder);
    public Folder updateFolder(Folder folder);
    public boolean removeFolder(String folderid);
    public void decrementIndexes(String parentFolderId, int index);
    public void incrementIndexes(String parentFolderId, int index);
}