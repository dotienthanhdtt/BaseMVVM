package com.example.baseapplication.presentation.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.baseapplication.R;
import com.example.baseapplication.common.utils.DialogUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseFragment<DB extends ViewDataBinding, VM extends BaseViewModel> extends Fragment
    implements View.OnClickListener {
    protected DB viewBinding;
    protected VM viewModel;
    protected SharedViewModel sharedViewModel;

    public abstract int getLayoutId();

    public abstract void initViewModel();

    /**
     * Initialize views
     */
    public abstract void initViews();

    public abstract void initObservers();

    /**
     * Initialize listeners
     */
    public abstract void initListeners();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initSharedViewModel();
        initViews();
        initBaseObservers();
        initObservers();
        initListeners();
    }

    private void initBaseObservers() {
        viewModel.getErrorModelLiveData().observe(getViewLifecycleOwner(), errorModel -> {
            if (errorModel.getMessage() == null) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), errorModel.getMessage(), Toast.LENGTH_SHORT).show();
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading == null) {
                return;
            }
            if (isLoading) {
                showProgressDialog(R.string.app_name);
                return;
            }
            dismissProgressDialog();
        });
    }

    private void initSharedViewModel() {
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
    }

    /**
     * This function to get ViewModel
     *
     * @param clazz
     * @return
     */
    protected VM getViewModel(Class<VM> clazz) {
        return new ViewModelProvider(this).get(clazz);
    }

    /**
     * This function to get parent ViewModel
     * 親ViewModelを取得するこの関数
     *
     * @param clazz
     * @param <PVM>
     * @return
     */
    protected <PVM extends ViewModel> PVM getParentViewModel(Class<PVM> clazz) {
        PVM parentViewModel = new ViewModelProvider(requireActivity()).get(clazz);
        return parentViewModel;
    }


    /**
     * This function is used to show soft keyboard
     * この機能は、ソフトキーボードを表示するために使用されます
     */
    public void showKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public void forceShowKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * This function is used to hide soft keyboard
     * この機能は、ソフトキーボードを非表示にするために使用されます
     */
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view == null) {
//            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
            return;
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showProgressDialog(String message) {
        DialogUtils.showProgressDialog(requireContext(), message);
    }

    public void showProgressDialog(@StringRes int messageId) {
        showProgressDialog(getString(messageId));
    }

    public void dismissProgressDialog() {
        DialogUtils.dismissProgressDialog();
    }
}
