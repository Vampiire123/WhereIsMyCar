package com.example.syl.whereismycar.ui.presenter;

import android.content.Context;

import com.example.syl.whereismycar.R;
import com.example.syl.whereismycar.datasource.db.DataLocationsDBImpl;
import com.example.syl.whereismycar.usecase.CheckPermission;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        givenAMockedStrings();

        presenter.initialize();

        verify(mockView).showToastMessage("Welcome back!");
    }

    @Test
    public void shouldShowPermissionsDialogOnStart(){
        givenPermissions(false);

        presenter.initialize();

        verify(mockView).showPermissionRequest();
    }

    private void givenAMockedStrings() {
        when(mockContext.getString(R.string.welcome_back)).thenReturn("Welcome back!");
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