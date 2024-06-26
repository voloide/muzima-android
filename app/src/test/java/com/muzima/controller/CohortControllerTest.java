/*
 * Copyright (c) The Trustees of Indiana University, Moi University
 * and Vanderbilt University Medical Center. All Rights Reserved.
 *
 * This version of the code is licensed under the MPL 2.0 Open Source license
 * with additional health care disclaimer.
 * If the user is an entity intending to commercialize any application that uses
 * this code in a for-profit venture, please contact the copyright holder.
 */

package com.muzima.controller;

import com.muzima.MuzimaApplication;
import com.muzima.api.model.Cohort;
import com.muzima.api.model.CohortData;
import com.muzima.api.model.LastSyncTime;
import com.muzima.api.model.MuzimaSetting;
import com.muzima.api.service.CohortService;
import com.muzima.api.service.LastSyncTimeService;
import com.muzima.api.service.MuzimaSettingService;
import com.muzima.api.service.SetupConfigurationService;

import com.muzima.service.SntpService;
import com.muzima.utils.StringUtils;
import org.apache.lucene.queryParser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.muzima.api.model.APIName.DOWNLOAD_COHORTS;
import static com.muzima.api.model.APIName.DOWNLOAD_COHORTS_DATA;
import static com.muzima.util.Constants.ServerSettings.DISPLAY_ONLY_COHORTS_IN_CONFIG_SETTING;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CohortControllerTest {
    private CohortController controller;
    private CohortService cohortService;
    private LastSyncTimeService lastSyncTimeService;
    private Date anotherMockDate;
    private SntpService sntpService;
    private MuzimaApplication muzimaApplication;
    private Date mockDate;

    @Before
    public void setup() throws MuzimaSettingController.MuzimaSettingFetchException {
        cohortService = mock(CohortService.class);
        lastSyncTimeService = mock(LastSyncTimeService.class);
        sntpService = mock(SntpService.class);
        muzimaApplication = mock(MuzimaApplication.class);
        controller = new CohortController(cohortService, lastSyncTimeService, sntpService, muzimaApplication);
        anotherMockDate = mock(Date.class);
        mockDate = mock(Date.class);

        SetupConfigurationService setupConfigurationService = mock(SetupConfigurationService.class);
        MuzimaSettingService muzimaSettingService = mock(MuzimaSettingService.class);
        MuzimaSettingController muzimaSettingController = new MuzimaSettingController(muzimaSettingService, lastSyncTimeService, sntpService, setupConfigurationService, muzimaApplication);
        MuzimaSetting muzimaSetting = new MuzimaSetting();
        muzimaSetting.setProperty(DISPLAY_ONLY_COHORTS_IN_CONFIG_SETTING);
        muzimaSetting.setValueBoolean(false);
        when(muzimaSettingController.getSettingByProperty(DISPLAY_ONLY_COHORTS_IN_CONFIG_SETTING)).thenReturn(muzimaSetting);
        when(muzimaApplication.getMuzimaSettingController()).thenReturn(muzimaSettingController);
    }

    @Test
    public void getAllCohorts_shouldReturnAllAvailableCohorts() throws IOException, CohortController.CohortFetchException {
        List<Cohort> cohorts = new ArrayList<>();
        when(cohortService.getAllCohorts()).thenReturn(cohorts);

        assertThat(controller.getAllCohorts(), is(cohorts));
    }

    @Test(expected = CohortController.CohortFetchException.class)
    public void getAllCohorts_shouldThrowCohortFetchExceptionIfExceptionThrownByCohortService() throws IOException, CohortController.CohortFetchException {
        doThrow(new IOException()).when(cohortService).getAllCohorts();
        controller.getAllCohorts();

        doThrow(new ParseException()).when(cohortService).getAllCohorts();
        controller.getAllCohorts();
    }

    @Test
    public void downloadAllCohorts_shouldReturnDownloadedCohorts() throws CohortController.CohortDownloadException, IOException {
        List<Cohort> downloadedCohorts = new ArrayList<>();
        List<Cohort> cohorts = new ArrayList<>();
        when(cohortService.downloadCohortsByName(StringUtils.EMPTY,null, null)).thenReturn(downloadedCohorts);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS)).thenReturn(null);
        controller.downloadAllCohorts(null);

        assertThat(controller.downloadAllCohorts(null), is(downloadedCohorts));
    }

    @Test(expected = CohortController.CohortDownloadException.class)
    public void downloadAllCohorts_shouldThrowCohortDownloadExceptionIfExceptionIsThrownByCohortService() throws CohortController.CohortDownloadException, IOException {
        doThrow(new IOException()).when(cohortService).downloadCohortsByNameAndSyncDate(StringUtils.EMPTY, null,null,null);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS)).thenReturn(null);
        controller.downloadAllCohorts(null);
    }

    @Test
    public void shouldSaveLastSyncTimeAfterDownloadingAllCohorts() throws Exception, CohortController.CohortDownloadException {
        ArgumentCaptor<LastSyncTime> lastSyncCaptor = ArgumentCaptor.forClass(LastSyncTime.class);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS)).thenReturn(anotherMockDate);
        when(sntpService.getTimePerDeviceTimeZone()).thenReturn(mockDate);

        controller.downloadAllCohorts(null);
        verify(lastSyncTimeService).saveLastSyncTime(lastSyncCaptor.capture());
        verify(lastSyncTimeService).getLastSyncTimeFor(DOWNLOAD_COHORTS);


        LastSyncTime setLastSyncTime = lastSyncCaptor.getValue();
        assertThat(setLastSyncTime.getApiName(), is(DOWNLOAD_COHORTS));
        assertThat(setLastSyncTime.getLastSyncDate(), is(mockDate));
        assertThat(setLastSyncTime.getParamSignature(), nullValue());
    }

    @Test
    public void shouldSaveLastSyncTimeAfterDownloadingAllCohortsWithPrefix() throws Exception, CohortController.CohortDownloadException {
        ArgumentCaptor<LastSyncTime> lastSyncCaptor = ArgumentCaptor.forClass(LastSyncTime.class);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS, "prefix1")).thenReturn(anotherMockDate);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS, "prefix2")).thenReturn(anotherMockDate);

        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS)).thenReturn(anotherMockDate);
        when(sntpService.getTimePerDeviceTimeZone()).thenReturn(mockDate);

        controller.downloadCohortsByPrefix(asList("prefix1", "prefix2"),null);
        verify(lastSyncTimeService, times(2)).saveLastSyncTime(lastSyncCaptor.capture());
        verify(lastSyncTimeService).getLastSyncTimeFor(DOWNLOAD_COHORTS, "prefix1");
        verify(lastSyncTimeService).getLastSyncTimeFor(DOWNLOAD_COHORTS, "prefix2");

        LastSyncTime firstSetLastSyncTime = lastSyncCaptor.getAllValues().get(0);
        LastSyncTime secondSetLastSyncTime = lastSyncCaptor.getAllValues().get(1);
        assertThat(firstSetLastSyncTime.getApiName(), is(DOWNLOAD_COHORTS));
        assertThat(firstSetLastSyncTime.getLastSyncDate(), is(mockDate));
        assertThat(firstSetLastSyncTime.getParamSignature(), is("prefix1"));
        assertThat(secondSetLastSyncTime.getApiName(), is(DOWNLOAD_COHORTS));
        assertThat(secondSetLastSyncTime.getLastSyncDate(), is(mockDate));
        assertThat(secondSetLastSyncTime.getParamSignature(), is("prefix2"));
    }

    @Test
    public void downloadCohortDataByUuid_shouldDownloadCohortByUuid() throws IOException, CohortController.CohortDownloadException {
        CohortData cohortData = new CohortData();
        String uuid = "uuid";
        when(cohortService.downloadCohortDataAndSyncDate(uuid, false, null, null, null)).thenReturn(cohortData);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS_DATA, uuid)).thenReturn(null);

        assertThat(controller.downloadCohortDataByUuid(uuid, null), is(cohortData));
    }

    @Test
    public void shouldGetLastSynchDateAndUseItWhenDownloadingData() throws IOException, CohortController.CohortDownloadException {
        CohortData cohortData = mock(CohortData.class);
        String uuid = "uuid";
        when(cohortService.downloadCohortData(uuid, false, null, null)).thenReturn(cohortData);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS_DATA, uuid)).thenReturn(mockDate);
        when(cohortService.downloadCohortDataAndSyncDate(uuid, false, mockDate, null, null)).thenReturn(cohortData);

        controller.downloadCohortDataByUuid(uuid, null);

        verify(lastSyncTimeService).getLastSyncTimeFor(DOWNLOAD_COHORTS_DATA, uuid);
        verify(cohortService,never()).downloadCohortData(uuid, false, null , null );
    }

    @Test
    public void shouldSaveLastSyncTimeOfCohortData() throws Exception, CohortController.CohortDownloadException {
        String uuid = "uuid";
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS_DATA, uuid)).thenReturn(mockDate);
        when(sntpService.getTimePerDeviceTimeZone()).thenReturn(anotherMockDate);

        controller.downloadCohortDataByUuid(uuid, null);

        ArgumentCaptor<LastSyncTime> captor = ArgumentCaptor.forClass(LastSyncTime.class);
        verify(lastSyncTimeService).saveLastSyncTime(captor.capture());
        LastSyncTime savedLastSyncTime = captor.getValue();
        assertThat(savedLastSyncTime.getApiName(), is(DOWNLOAD_COHORTS_DATA));
        assertThat(savedLastSyncTime.getLastSyncDate(), is(anotherMockDate));
        assertThat(savedLastSyncTime.getParamSignature(), is(uuid));
    }

    @Test(expected = CohortController.CohortDownloadException.class)
    public void downloadCohortDataByUuid_shouldThrowCohortDownloadExceptionIfExceptionThrownByCohortService() throws IOException, CohortController.CohortDownloadException {
        String uuid = "uuid";
        doThrow(new IOException()).when(cohortService).downloadCohortDataAndSyncDate(uuid, false, null, null, null);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS_DATA, uuid)).thenReturn(null);

        controller.downloadCohortDataByUuid(uuid, null);
    }

    @Test
    public void downloadCohortDataAndSyncDate_shouldDownloadDeltaCohortDataBySyncDate() throws IOException, CohortController.CohortDownloadException, ProviderController.ProviderLoadException {
        String[] uuids = new String[]{"uuid1", "uuid2"};
        CohortData cohortData1 = new CohortData();
        CohortData cohortData2 = new CohortData();
        when(cohortService.downloadCohortDataAndSyncDate(uuids[0], false, null, null, null)).thenReturn(cohortData1);
        when(cohortService.downloadCohortDataAndSyncDate(uuids[1], false, null, null ,null)).thenReturn(cohortData2);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS_DATA, "uuid1")).thenReturn(null);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS_DATA, "uuid2")).thenReturn(null);

        List<CohortData> allCohortData = controller.downloadCohortData(uuids, null);
        assertThat(allCohortData.size(), is(2));
        assertThat(allCohortData, hasItem(cohortData1));
        assertThat(allCohortData, hasItem(cohortData2));
    }

    @Test
    public void downloadCohortsByPrefix_shouldDownloadAllCohortsForTheGivenPrefixes() throws IOException, CohortController.CohortDownloadException {
        List<String> cohortPrefixes = new ArrayList<String>() {{
            add("Age");
            add("age");
            add("Encounter");
        }};

        Cohort cohort11 = new Cohort() {{
            setUuid("uuid1");
            setName("Age between 20 and 30");
        }};
        Cohort cohort12 = new Cohort() {{
            setUuid("uuid1");
            setName("Age between 20 and 30");
        }};

        Cohort cohort2 = new Cohort() {{
            setUuid("uuid2");
            setName("Patients with age over 65");
        }};
        Cohort cohort3 = new Cohort() {{
            setUuid("uuid3");
            setName("Encounter 1");
        }};
        Cohort cohort4 = new Cohort() {{
            setUuid("uuid4");
            setName("Encounter 2");
        }};


        ArrayList<Cohort> agePrefixedCohortList1 = new ArrayList<>();
        agePrefixedCohortList1.add(cohort11);
        agePrefixedCohortList1.add(cohort2);

        ArrayList<Cohort> agePrefixedCohortList2 = new ArrayList<>();
        agePrefixedCohortList2.add(cohort12);
        agePrefixedCohortList2.add(cohort2);

        ArrayList<Cohort> encounterPerfixedCohortList = new ArrayList<>();
        encounterPerfixedCohortList.add(cohort3);
        encounterPerfixedCohortList.add(cohort4);

        when(cohortService.downloadCohortsByNameAndSyncDate(cohortPrefixes.get(0), mockDate, null, null)).thenReturn(agePrefixedCohortList1);
        when(cohortService.downloadCohortsByNameAndSyncDate(cohortPrefixes.get(1), anotherMockDate, null, null)).thenReturn(agePrefixedCohortList2);
        when(cohortService.downloadCohortsByNameAndSyncDate(cohortPrefixes.get(2), anotherMockDate, null, null)).thenReturn(encounterPerfixedCohortList);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS, cohortPrefixes.get(0))).thenReturn(anotherMockDate);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS, cohortPrefixes.get(1))).thenReturn(anotherMockDate);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS, cohortPrefixes.get(2))).thenReturn(anotherMockDate);
        when(lastSyncTimeService.getLastSyncTimeFor(DOWNLOAD_COHORTS)).thenReturn(anotherMockDate);
        when(sntpService.getTimePerDeviceTimeZone()).thenReturn(mockDate);

        List<Cohort> downloadedCohorts = controller.downloadCohortsByPrefix(cohortPrefixes,null);
        assertThat(downloadedCohorts.size(), is(3));
        assertTrue(downloadedCohorts.contains(cohort11));
        assertTrue(downloadedCohorts.contains(cohort3));
        assertTrue(downloadedCohorts.contains(cohort4));
    }

    @Test
    public void saveAllCohorts_shouldSaveAllCohorts() throws CohortController.CohortSaveException, IOException {
        ArrayList<Cohort> cohorts = new ArrayList<Cohort>() {{
            add(new Cohort());
            add(new Cohort());
            add(new Cohort());
        }};

        controller.saveAllCohorts(cohorts);

        verify(cohortService).saveCohorts(cohorts);
        verifyNoMoreInteractions(cohortService);
    }

    @Test(expected = CohortController.CohortSaveException.class)
    public void saveAllCohorts_shouldThrowCohortSaveExceptionIfExceptionThrownByCohortService() throws IOException, CohortController.CohortSaveException {
        ArrayList<Cohort> cohorts = new ArrayList<Cohort>() {{
            add(new Cohort());
        }};
        doThrow(new IOException()).when(cohortService).saveCohorts(cohorts);

        controller.saveAllCohorts(cohorts);
    }

    @Test
    public void deleteAllCohorts_shouldDeleteAllCohorts() throws IOException, CohortController.CohortDeleteException {
        ArrayList<Cohort> cohorts = new ArrayList<Cohort>() {{
            add(new Cohort());
            add(new Cohort());
        }};
        when(cohortService.getAllCohorts()).thenReturn(cohorts);

        controller.deleteAllCohorts();

        verify(cohortService).getAllCohorts();
        verify(cohortService).deleteCohorts(cohorts);
        verifyNoMoreInteractions(cohortService);
    }

    @Test(expected = CohortController.CohortDeleteException.class)
    public void deleteAllCohorts_shouldThrowCohortSaveExceptionIfExceptionThrownByCohortService() throws IOException, CohortController.CohortDeleteException {
        ArrayList<Cohort> cohorts = new ArrayList<Cohort>() {{
            add(new Cohort());
            add(new Cohort());
        }};
        when(cohortService.getAllCohorts()).thenReturn(cohorts);
        doThrow(new IOException()).when(cohortService).deleteCohorts(cohorts);

        controller.deleteAllCohorts();
    }

    @Test
    public void getTotalCohortsCount_shouldReturnEmptyListOfNoCohortsHaveBeenSynced() throws IOException, CohortController.CohortFetchException {
        when(cohortService.countAllCohorts()).thenReturn(2);
        assertThat(controller.countAllCohorts(), is(2));
    }


    @Test
    public void getSyncedCohortsCount_shouldReturnTotalNumberOfSyncedCohorts() throws IOException, CohortController.CohortFetchException {
        List<Cohort> cohorts = new ArrayList<>();
        Cohort cohort1 = new Cohort() {{
            setUuid("uuid1");
            setSyncStatus(1);
        }};

        Cohort cohort2 = new Cohort() {{
            setUuid("uuid2");
            setSyncStatus(1);
        }};

        Cohort cohort3 = new Cohort() {{
            setUuid("uuid3");
            setSyncStatus(0);
        }};

        cohorts.add(cohort1);
        cohorts.add(cohort2);
        cohorts.add(cohort3);

        when(cohortService.getAllCohorts()).thenReturn(cohorts);
        when(cohortService.getCohortByUuid("uuid1")).thenReturn(cohort1);
        when(cohortService.getCohortByUuid("uuid2")).thenReturn(cohort2);
        when(cohortService.getCohortByUuid("uuid3")).thenReturn(cohort3);
        assertThat(controller.countSyncedCohorts(), is(2));
    }

    @Test
    public void getSyncedCohortsCount_shouldReturnZeroIfNoCohortIsSynced() throws IOException, CohortController.CohortFetchException {
        List<Cohort> cohorts = new ArrayList<>();

        when(cohortService.getAllCohorts()).thenReturn(cohorts);

        assertThat(controller.countSyncedCohorts(), is(0));
    }

    @Test
    public void getSyncedCohorts_shouldReturnTheCohortsWhichHaveSyncStatusOne() throws IOException, CohortController.CohortFetchException {
        List<Cohort> cohorts = new ArrayList<>();
        Cohort cohort1 = new Cohort() {{
            setUuid("uuid1");
            setSyncStatus(1);
        }};

        Cohort cohort2 = new Cohort() {{
            setUuid("uuid2");
            setSyncStatus(1);
        }};

        Cohort cohort3 = new Cohort() {{
            setUuid("uuid3");
            setSyncStatus(0);
        }};

        cohorts.add(cohort1);
        cohorts.add(cohort2);
        cohorts.add(cohort3);

        when(cohortService.getAllCohorts()).thenReturn(cohorts);
        when(cohortService.getCohortByUuid("uuid1")).thenReturn(cohort1);
        when(cohortService.getCohortByUuid("uuid2")).thenReturn(cohort2);
        when(cohortService.getCohortByUuid("uuid3")).thenReturn(cohort3);
        assertThat(controller.getSyncedCohorts(), hasItem(cohort1));
        assertThat(controller.getSyncedCohorts(), hasItem(cohort2));
        assertThat(controller.getSyncedCohorts(), not(hasItem(cohort3)));
    }

    @Test
    public void getSyncedCohorts_shouldNotReturnCohortsIfTheyHaveSyncStatusZero() throws IOException, CohortController.CohortFetchException {
        List<Cohort> cohorts = new ArrayList<>();
        Cohort cohort1 = new Cohort() {{
            setUuid("uuid1");
            setSyncStatus(0);
        }};

        Cohort cohort2 = new Cohort() {{
            setUuid("uuid2");
            setSyncStatus(0);
        }};

        cohorts.add(cohort1);
        cohorts.add(cohort2);

        when(cohortService.getAllCohorts()).thenReturn(cohorts);
        when(cohortService.getCohortByUuid("uuid1")).thenReturn(cohort1);
        when(cohortService.getCohortByUuid("uuid2")).thenReturn(cohort2);

        assertThat(controller.getSyncedCohorts().size(), is(0));
    }
}
