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
package edu.mit.ll.nics.nicsdao.impl;

import edu.mit.ll.dao.QueryBuilder;
import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.QueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.ll.nics.common.entity.Uxoreport;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.UxoreportDAO;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cbudny on 1/27/16.
 */
public class UxoreportDAOImpl extends GenericDAO implements UxoreportDAO
{
    private Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
        log = LoggerFactory.getLogger(UxoreportDAOImpl.class);
        try
        {
            this.template = new NamedParameterJdbcTemplate(datasource);
        } catch (Exception e)
        {
            e.printStackTrace();
            log.warn("Exception initializing datasource! Continuing to allow for setting manually with "
                    + "setDataSource()");
        }
    }

    @Override
    public Uxoreport persistUxoreport(Uxoreport report) throws Exception
    {
        if (report == null)
        {
            log.debug("Received null Uxoreport entity, not persisting");
            return null;
        }

        boolean update = false;
        Uxoreport newReport = null;

        long reportId = report.getUxoreportid();
        Uxoreport oldReport = null;

        if (reportId > 0)
        {
            try
            {
                oldReport = getUxoreport(reportId);
            } catch (Exception e)
            {
                log.error("Exception retriving uxoreport with id: " + reportId + ": " + e.getMessage());
            }
        }

        if (oldReport != null)
        {
//            update = true;
//            log.debug("Updating UXOREPORT with id: " + reportId);
        }

        if (update)
        {
            QueryModel query = QueryManager.createQuery(SADisplayConstants.UXOREPORT_TABLE).update()
                    .equals(SADisplayConstants.UXOREPORTID).comma()
                    .equals(SADisplayConstants.INCIDENT_ID).comma()
                    .equals("lat").comma()
                    .equals("lon").comma()
                    .equals(SADisplayConstants.MESSAGE)
                    .where().equals(SADisplayConstants.UXOREPORTID);

//            BeanPropertySqlParameterSource map = new BeanPropertySqlParameterSource(report);
//            int ret = -1;
//
//            try
//            {
//                ret = this.template.update(query.toString(), map);
//            } catch (Exception e)
//            {
//                throw new Exception ("Error updating uxoreport with id: "+ reportId+": " + e.getMessage());
//            }

//            log.debug("Update performed on uxoreport("+ reportId+") resulted in "+
//                    ((ret == 0) ? "no" : ret) + " affected rows.");
//
//            if (ret == 1)
//            {
//                newReport = getUxoreport(reportId);
//            }
//
//            return newReport;
        } else
        {
            try
            {
                List<String> fields = Arrays.asList(
                        "incidentid", "message",
                        "lat", "lon");

                QueryModel query = QueryManager.createQuery(SADisplayConstants.UXOREPORT_TABLE)
                        .insertInto(fields);


//                MapSqlParameterSource map = new MapSqlParameterSource()
//                        .addValue(SADisplayConstants.INCIDENT_ID, report.getIncidentid())
//                        .addValue("lat", report.getLat())
//                        .addValue("lon", report.getLon())
//                        .addValue(SADisplayConstants.MESSAGE, report.getMessage());
                BeanPropertySqlParameterSource map = new BeanPropertySqlParameterSource(report);
                this.template.update(query.toString(), map);
                return report;
            } catch (Exception e)
            {
                throw new Exception("Unhandled exception persisting new entity: " + e.getMessage());
            }
        }


        return null;
    }

    @Override
    public Uxoreport getUxoreport(long uxoreportid) {
        return null;
    }

    @Override
    public List<Uxoreport> getUxoreports(int incidentid) {
        return null;
    }

    @Override
    public long updateUxoreportMessage(Uxoreport report) throws Exception {
        return 0;
    }
}
