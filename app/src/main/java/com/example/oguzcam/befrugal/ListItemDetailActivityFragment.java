package com.example.oguzcam.befrugal;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.oguzcam.befrugal.enums.BaseAmount;
import com.example.oguzcam.befrugal.enums.UnitType;
import com.example.oguzcam.befrugal.model.ListContract;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListItemDetailActivityFragment extends Fragment {

    private static final String TAG = ListItemActivityFragment.class.getSimpleName();

    private static final String QTY = "QTY";
    private static final String UNIT = "UNIT";
    private static final String TOTAL = "TOTAL";
    private String lastFocus = QTY;

    static final String LIST_ITEM_DETAIL_URI = "LID_URI";
    static final String LIST_ITEM_DETAIL_LIST_ID = "LID_LIST_ID";

    private Uri mUri;
    private long mListItemId;
    private long mListId;
    private String mFirstItemName;

    private EditText mListItemNameView;
    private EditText mBoughtQtyView;
    private EditText mUnitAmountView;
    private EditText mTotalAmountView;
    private Spinner mUnitTypeSpinner;
    private CheckBox mListItemDoneCheckBox;

    private BaseAmount mBaseAmount;

    private final int COL_LIST_ITEM_ID = 0;
    private final int COL_LIST_ITEM_NAME = 1;
    private final int COL_QUANTITY = 2;
    private final int COL_UNIT_AMOUNT = 3;
    private final int COL_UNIT_TYPE = 4;
    private final int COL_TOTAL_AMOUNT = 5;
    private final int COL_LIST_ID = 6;

    private final String[] projection = new String[] {
            ListContract.ListItemEntry.TABLE_NAME + "." + ListContract.ListItemEntry._ID,
            ListContract.ListItemEntry.COLUMN_LIST_ITEM_NAME,
            ListContract.ListItemEntry.COLUMN_QUANTITY,
            ListContract.ListItemEntry.COLUMN_UNIT_AMOUNT,
            ListContract.ListItemEntry.COLUMN_UNIT_TYPE,
            ListContract.ListItemEntry.COLUMN_TOTAL_AMOUNT,
            ListContract.ListItemEntry.COLUMN_LIST_ID
    };

    public ListItemDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list_item_detail_fragment, container, false);

        mListItemNameView = (EditText) rootView.findViewById(R.id.list_item_name);
        mBoughtQtyView = (EditText) rootView.findViewById(R.id.bought_qty);
        mUnitAmountView = (EditText) rootView.findViewById(R.id.unit_amount);
        mTotalAmountView = (EditText) rootView.findViewById(R.id.total_amount);
        // set spinner
        mUnitTypeSpinner = (Spinner) rootView.findViewById(R.id.unit_type);
        mListItemDoneCheckBox = (CheckBox) rootView.findViewById(R.id.list_item_done);

        ArrayAdapter<String> unitTypeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                UnitType.getFriendlyNames());
        mUnitTypeSpinner.setAdapter(unitTypeAdapter);
        Button saveBtn = (Button) rootView.findViewById(R.id.list_item_detail_save);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(LIST_ITEM_DETAIL_URI);
            if(mUri != null) {
                // Update selected record
                Log.v(TAG, "mUri = " + mUri);
                mListItemId = ListContract.ListItemEntry.getListIdFromUri(mUri);
                Log.v(TAG, "mListItemId = " + mListItemId);

                Cursor cursor = getActivity().getContentResolver().query(
                        mUri,
                        projection,
                        null,
                        null,
                        null
                );

                if (cursor != null && cursor.moveToFirst()) {
                    mFirstItemName = cursor.getString(COL_LIST_ITEM_NAME);
                    mListItemNameView.setText(mFirstItemName);
                    mListId = cursor.getLong(COL_LIST_ID);
                    mBoughtQtyView.setText(cursor.getString(COL_QUANTITY));
                    mUnitAmountView.setText(asAmount(cursor.getDouble(COL_UNIT_AMOUNT)));
                    int spinnerPosition = unitTypeAdapter.getPosition(cursor.getString(COL_UNIT_TYPE));
                    mUnitTypeSpinner.setSelection(spinnerPosition);
                    mTotalAmountView.setText(asAmount(cursor.getDouble(COL_TOTAL_AMOUNT)));
                    saveBtn.setText(getString(R.string.save));
                    cursor.close();
                }
            } else {
                // New record for that list
                mListId = arguments.getLong(LIST_ITEM_DETAIL_LIST_ID);
            }
        }

        mBoughtQtyView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    calculateAmount(QTY);
                } else {
                    lastFocus = QTY;
                }
            }
        });

        mUnitAmountView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    calculateAmount(UNIT);
                } else {
                    lastFocus = UNIT;
                }
            }
        });

        mTotalAmountView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calculateAmount(TOTAL);
                } else {
                    lastFocus = TOTAL;
                }
            }
        });

        saveBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate
                if (mListItemNameView.getText().toString().trim().isEmpty()) {
                    Snackbar.make(
                            rootView,
                            getString(R.string.error_list_item_name_empty),
                            Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                // Calculate according to last state
                calculateAmount(lastFocus);

                // Update if existing, create otherwise
                if (mListItemId != 0) {
                    Log.v(TAG, "Update list item");
                    Snackbar.make(
                            rootView,
                            getString(R.string.update_snackbar, mFirstItemName, String.valueOf(mListItemNameView.getText())),
                            Snackbar.LENGTH_SHORT)
                            .show();
                    String where = ListContract.ListItemEntry._ID + "=?";
                    getActivity().getContentResolver().update(
                            ListContract.ListItemEntry.CONTENT_URI,
                            getContentValues(),
                            where,
                            new String[]{Long.toString(mListItemId)}
                    );
                } else {
                    Log.v(TAG, "Create list item");
                    Snackbar.make(
                            rootView,
                            getString(R.string.add_snackbar, String.valueOf(mListItemNameView.getText())),
                            Snackbar.LENGTH_SHORT)
                            .show();
                    getActivity().getContentResolver().insert(
                            ListContract.ListItemEntry.CONTENT_URI,
                            getContentValues()
                    );
                }
                try {
                    wait(Snackbar.LENGTH_SHORT);
                } catch (Exception ex) {
                    StringWriter errors = new StringWriter();
                    ex.printStackTrace(new PrintWriter(errors));
                    Log.d(TAG, errors.toString());
                    getActivity().finish();
                }
                getActivity().finish();
            }
        });

        return rootView;
    }

    private String asAmount(BigDecimal amount) {
        return String.format("%.2f", amount);
    }

    private String asAmount(Double amount) {
        return String.format("%.2f", amount);
    }

    // Calculate other amount when one of them (total - unit) filled
    private void calculateAmount(String field) {
        final String qtyText = mBoughtQtyView.getText().toString();
        final String unitAmountText = mUnitAmountView.getText().toString();
        final String totalAmountText = mTotalAmountView.getText().toString();

        // No baseAmount
        if (qtyText.isEmpty()
                ||
                (mBaseAmount == BaseAmount.UNIT
                        && unitAmountText.isEmpty())
                ||
                (mBaseAmount == BaseAmount.TOTAL
                        && totalAmountText.isEmpty())
                ) {
            mBaseAmount = null;
            return;
        }

        if (field.equals(QTY)) {
            if (mBaseAmount == null) {
                if (!unitAmountText.isEmpty()) {
                    field = UNIT;
                } else if (!totalAmountText.isEmpty()) {
                    field = TOTAL;
                } else {
                    return;
                }
            }

            if (mBaseAmount == BaseAmount.UNIT) {
                field = UNIT;
            } else if (mBaseAmount == BaseAmount.TOTAL) {
                field = TOTAL;
            }
        }

        // Calculate total amount
        if (field.equals(UNIT) && mUnitAmountView.isEnabled()) {
            if (!qtyText.isEmpty() &&
                    !unitAmountText.isEmpty()
                    ) {
                BigDecimal qty = new BigDecimal(qtyText);
                BigDecimal unitAmount = new BigDecimal(unitAmountText);
                mTotalAmountView.setText(asAmount(unitAmount.multiply(qty)));
                mTotalAmountView.setEnabled(false);

                mBaseAmount = BaseAmount.UNIT;

                // Check done box if amounts entered
                checkDone(true);
            } else {
                mTotalAmountView.setText("");
                mTotalAmountView.setEnabled(true);
                checkDone(false);
            }
        } else if (field.equals(TOTAL) && mTotalAmountView.isEnabled()) {
            // Calculate unit amount
            if (!qtyText.isEmpty()
                    && !totalAmountText.isEmpty()
                    ) {
                BigDecimal boughtQty = new BigDecimal(qtyText);
                BigDecimal totalAmount = new BigDecimal(totalAmountText);
                mUnitAmountView.setText(asAmount(totalAmount.divide(boughtQty, 2, RoundingMode.HALF_UP)));
                mUnitAmountView.setEnabled(false);

                mBaseAmount = BaseAmount.TOTAL;

                // Check done box if amounts entered
                checkDone(true);
            } else {
                mUnitAmountView.setText("");
                mUnitAmountView.setEnabled(true);
                checkDone(false);
            }
        }
    }
    // Check done box if amount entered
    private void checkDone(boolean canBeDone) {
        if (canBeDone && !mListItemDoneCheckBox.isEnabled()) {
            mListItemDoneCheckBox.setChecked(true);
            mListItemDoneCheckBox.setEnabled(true);
        } else if (!canBeDone) {
            mListItemDoneCheckBox.setChecked(false);
            mListItemDoneCheckBox.setEnabled(false);
        }
    }

    private ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(ListContract.ListItemEntry.COLUMN_CREATION_DATE, new Date().getTime());
        values.put(ListContract.ListItemEntry.COLUMN_LIST_ITEM_NAME, String.valueOf(mListItemNameView.getText()));
        values.put(ListContract.ListItemEntry.COLUMN_UNIT_TYPE, String.valueOf(mUnitTypeSpinner.getSelectedItem()));
        values.put(ListContract.ListItemEntry.COLUMN_LIST_ID, mListId);

        if(!mBoughtQtyView.getText().toString().isEmpty()) {
            values.put(ListContract.ListItemEntry.COLUMN_QUANTITY, Double.parseDouble(mBoughtQtyView.getText().toString()));
        } else {
            values.put(ListContract.ListItemEntry.COLUMN_QUANTITY, (Double) null);
        }
        if(!mUnitAmountView.getText().toString().isEmpty()) {
            values.put(ListContract.ListItemEntry.COLUMN_UNIT_AMOUNT, Double.parseDouble(mUnitAmountView.getText().toString()));
        } else {
            values.put(ListContract.ListItemEntry.COLUMN_UNIT_AMOUNT, (Double) null);
        }
        if(!mTotalAmountView.getText().toString().isEmpty()) {
            values.put(ListContract.ListItemEntry.COLUMN_TOTAL_AMOUNT, Double.parseDouble(mTotalAmountView.getText().toString()));
        } else {
            values.put(ListContract.ListItemEntry.COLUMN_TOTAL_AMOUNT, (Double) null);
        }
        return values;
    }
}
