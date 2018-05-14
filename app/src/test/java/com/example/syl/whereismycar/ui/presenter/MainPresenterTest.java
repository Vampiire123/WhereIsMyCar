package com.example.syl.whereismycar.ui.presenter;

import android.content.Context;

import com.example.syl.whereismycar.R;
import com.example.syl.whereismycar.datasource.db.DataLocationsDBImpl;
import com.example.syl.whereismycar.global.model.MLocation;
import com.example.syl.whereismycar.usecase.CheckPermission;
import com.example.syl.whereismycar.usecase.DataLocations;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class MainPresenterTest {

    MainPresenter presenter;

    @Mock
    MainPresenter.View mockView;

    @Mock
    MainPresenter.Navigator mockNavigator;

    @Mock
    CheckPermission mockCheckPermission;

    @Mock
    Context mockContext;

    @Mock
    DataLocationsDBImpl mockDataLocationsDBImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        presenter = givenAMockedPresenter();
    }

    @Test
    public void shouldShowWelcomeMessageOnStart(){
        givenMockedStrings();

        presenter.initialize();

        verify(mockView).showMessage("Welcome back!");
    }

    @Test
    public void shouldShowPermissionsDialogOnStart(){
        givenPermissions(false);

        presenter.initialize();

        verify(mockView).showPermissionRequest();
    }

    @Test
    public void shouldShowActualLocationOnStart(){
        givenPermissions(true);
        givenCurrentLocation(new MLocation());

        presenter.initialize();

        verify(mockView).showActualLocation(any(MLocation.class));
    }

    @Test
    public void shouldShowErrorIfDBReturnsErrorInsteadLocationOnStart(){
        givenPermissions(true);
        givenErrorGettingActualLocation();
        givenMockedStrings();

        presenter.initialize();

        verify(mockView).showMessage("Error getting actual location");
    }

    @Test
    public void shouldShowSaveLocationMessageWhenSaveLocationButtonIsClicked(){
        givenMockedStrings();
        givenSaveLocationToDB(new MLocation());

        presenter.onSaveLocationButtonClicked();

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockView).showMessage(stringCaptor.capture());

        assertEquals(stringCaptor.getValue(), "Saved location MLocation{id=0, latitude=0.0, longitude=0.0, address=null}");
    }

    @Test
    public void shouldShowErrorMessageWhenSaveLocationButtonIsClickedAndDBReturnsError(){
        givenMockedStrings();
        givenErrorSavingLocationToDB();

        presenter.onSaveLocationButtonClicked();

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockView).showMessage(stringCaptor.capture());

        assertEquals(stringCaptor.getValue(), "Error saving location");
    }

    private void givenErrorSavingLocationToDB() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                DataLocations.Listener listener = (DataLocations.Listener) invocation.getArguments()[1];
                listener.onError();
                return null;
            }
        }).when(mockDataLocationsDBImpl).saveLocationToDB(any(MLocation.class), any(DataLocations.Listener.class));
    }

    private void givenSaveLocationToDB(final MLocation location) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                DataLocations.Listener listener = (DataLocations.Listener) invocation.getArguments()[1];
                listener.onSuccess(location);
                return null;
            }
        }).when(mockDataLocationsDBImpl).saveLocationToDB(any(MLocation.class), any(DataLocations.Listener.class));
    }

    private void givenErrorGettingActualLocation() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                DataLocations.Listener listener = (DataLocations.Listener) invocation.getArguments()[0];
                listener.onError();
                return null;
            }
        }).when(mockDataLocationsDBImpl).getCurrentLocation(any(DataLocations.Listener.class));
    }

    private void givenCurrentLocation(final MLocation location) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                DataLocations.Listener listener = (DataLocations.Listener) invocation.getArguments()[0];
                listener.onSuccess(location);
                return null;
            }
        }).when(mockDataLocationsDBImpl).getCurrentLocation(any(DataLocations.Listener.class));
    }

    private void givenMockedStrings() {
        when(mockContext.getString(R.string.error_getting_location)).thenReturn("Error getting actual location");
        when(mockContext.getString(R.string.welcome_back)).thenReturn("Welcome back!");
        when(mockContext.getString(R.string.saved_location)).thenReturn("Saved location");
        when(mockContext.getString(R.string.error_saving_location)).thenReturn("Error saving location");
    }

    private void givenPermissions(boolean permission) {
        when(mockCheckPermission.isPermissionGranted(anyString())).thenReturn(permission);
    }

    private MainPresenter givenAMockedPresenter() {
        MainPresenter mainPresenter = new MainPresenter(mockContext, mockDataLocationsDBImpl, mockCheckPermission);
        mainPresenter.setView(mockView);
        mainPresenter.setNavigator(mockNavigator);

        return mainPresenter;
    }
}