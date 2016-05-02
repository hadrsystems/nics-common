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
import java.util.Map;

import edu.mit.ll.nics.common.entity.datalayer.Datalayer;
import edu.mit.ll.nics.common.entity.datalayer.Datalayerfolder;
import edu.mit.ll.nics.common.entity.datalayer.Datasource;

public interface DatalayerDAO extends BaseDAO {
	public List<Datalayerfolder> getDatalayerFolders(String folderid);
	public List<Datasource> getDatasources(String type);
	public Datalayer reloadDatalayer(String datalayerid);
	public int getDatasourceTypeId(String datasourcetype);
	public String getDatasourceId(String internalurl);
	public String getDatalayersourceId(String layername);
	public String getUnofficialDatalayerId(String collabroom, String folderid);
	public List<String> getAvailableStyles();
	public Datalayerfolder getDatalayerfolder(String datalayerid, String folderid);
	public Datalayerfolder getDatalayerfolder(int datalayerfolderId);
	public int getNextDatalayerFolderId();
	public String insertDataSource(Datasource source);
	public String insertDataLayer(String dataSourceId, Datalayer datalayer);
	public boolean removeDataLayer(String dataSourceId);
	public Datalayer updateDataLayer(Datalayer datalayer);
	public int insertDataLayerFolder(String folderId, String datalayerId, int folderIndex);
	public Datasource getDatasource(String datasourceId);
	public Datalayerfolder updateDatalayerfolder(Datalayerfolder dlFolder);
	public void decrementIndexes(String parentFolderId, int index);
	public void incrementIndexes(String parentFolderId, int index);
	public int getNextDatalayerFolderIndex(String folderid);
	public List<Map<String, Object>> getAuthentication(String datasourceid);
}
