package com.oguzcam.befrugal;

import android.app.AlertDialog;
import android.app.LauncherActivity;
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

import com.oguzcam.befrugal.enums.BaseAmount;
import com.oguzcam.befrugal.enums.UnitType;
import com.oguzcam.befrugal.model.ListContract;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private static final int UPDATE = 0;
    private static final int INSERT = 1;

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
    private double mOldTotalAmount;
    private long mOldDone;

    private final int COL_LIST_ITEM_ID = 0;
    private final int COL_LIST_ITEM_NAME = 1;
    private final int COL_QUANTITY = 2;
    private final int COL_UNIT_AMOUNT = 3;
    private final int COL_UNIT_TYPE = 4;
    private final int COL_TOTAL_AMOUNT = 5;
    private final int COL_LIST_ID = 6;
    private final int COL_DONE = 7;

    private final String[] projection = new String[] {
            ListContract.ListItemEntry.TABLE_NAME + "." + ListContract.ListItemEntry._ID,
            ListContract.ListItemEntry.COLUMN_LIST_ITEM_NAME,
            ListContract.ListItemEntry.COLUMN_QUANTITY,
            ListContract.ListItemEntry.COLUMN_UNIT_AMOUNT,
            ListContract.ListItemEntry.COLUMN_UNIT_TYPE,
            ListContract.ListItemEntry.COLUMN_TOTAL_AMOUNT,
            ListContract.ListItemEntry.COLUMN_LIST_ID,
            ListContract.ListItemEntry.COLUMN_DONE
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
                    mUnitAmountView.setText(Utility.asAmount(cursor.getDouble(COL_UNIT_AMOUNT)));
                    int spinnerPosition = unitTypeAdapter.getPosition(cursor.getString(COL_UNIT_TYPE));
                    mUnitTypeSpinner.setSelection(spinnerPosition);
                    double totalAmount = cursor.getDouble(COL_TOTAL_AMOUNT);
                    mTotalAmountView.setText(Utility.asAmount(totalAmount));
                    mOldTotalAmount = totalAmount;
                    mOldDone = cursor.getLong(COL_DONE);
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

                Double difference = null;

                String totalAmountText = mTotalAmountView.getText().toString();

                // Update if existing, create if not
                if (mListItemId != 0) {
                    Log.v(TAG, getString(R.string.update_snackbar, mFirstItemName, String.valueOf(mListItemNameView.getText())));

                    String where = ListContract.ListItemEntry._ID + "=?";
                    getActivity().getContentResolver().update(
                            ListContract.ListItemEntry.CONTENT_URI,
                            getContentValues(UPDATE),
                            where,
                            new String[]{Long.toString(mListItemId)}
                    );

                    if (mListItemDoneCheckBox.isChecked()
                            && (!totalAmountText.isEmpty()
                            || mOldTotalAmount > 0)
                            ) {
                        if(totalAmountText.isEmpty()) {
                            totalAmountText = "0";
                        }

                        double totalAmount = Double.parseDouble(totalAmountText);
                        if (mOldDone == 1) {
                            if (totalAmount != mOldTotalAmount) {
                                difference = totalAmount - mOldTotalAmount;
                                Utility.addToListTotalAmount(getContext(), mListId, difference);
                            }
                        } else {
                            Utility.addToListTotalAmount(getContext(), mListId, totalAmount);
                        }
                    } else {
                        if (mOldDone == 1 && mOldTotalAmount != 0) {
                            Utility.addToListTotalAmount(getContext(), mListId, mOldTotalAmount * -1);
                        }
                    }

                } else {
                    Log.v(TAG, getString(R.string.add_snackbar, String.valueOf(mListItemNameView.getText())));

                    getActivity().getContentResolver().insert(
                            ListContract.ListItemEntry.CONTENT_URI,
                            getContentValues(INSERT)
                    );

                    if (mListItemDoneCheckBox.isChecked()
                            && !totalAmountText.isEmpty()) {
                        difference = Double.parseDouble(totalAmountText);
                        Utility.addToListTotalAmount(getContext(), mListId, difference);
                    }
                }
                getActivity().finish();
            }
        });

        return rootView;
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
            if (!unitAmountText.isEmpty()) {
                BigDecimal qty = new BigDecimal(qtyText);
                BigDecimal unitAmount = new BigDecimal(unitAmountText);
                mTotalAmountView.setText(Utility.asAmount(unitAmount.multiply(qty)));
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
            if (!totalAmountText.isEmpty()) {
                BigDecimal boughtQty = new BigDecimal(qtyText);
                BigDecimal totalAmount = new BigDecimal(totalAmountText);
                mUnitAmountView.setText(Utility.asAmount(totalAmount.divide(boughtQty, 2, RoundingMode.HALF_UP)));
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

    private ContentValues getContentValues(int mode) {
        ContentValues values = new ContentValues();
        long time = new Date().getTime();
        if (mode == INSERT) {
            values.put(ListContract.ListItemEntry.COLUMN_CREATION_TIME, time);
        }

        values.put(ListContract.ListItemEntry.COLUMN_LAST_UPDATED_TIME, time);
        values.put(ListContract.ListItemEntry.COLUMN_LIST_ITEM_NAME, String.valueOf(mListItemNameView.getText()));
        values.put(ListContract.ListItemEntry.COLUMN_UNIT_TYPE, String.valueOf(mUnitTypeSpinner.getSelectedItem()));
        values.put(ListContract.ListItemEntry.COLUMN_LIST_ID, mListId);

        if (!mBoughtQtyView.getText().toString().isEmpty()) {
            values.put(ListContract.ListItemEntry.COLUMN_QUANTITY, Double.parseDouble(mBoughtQtyView.getText().toString()));
        } else {
            values.put(ListContract.ListItemEntry.COLUMN_QUANTITY, (Double) null);
        }
        if (!mUnitAmountView.getText().toString().isEmpty()) {
            values.put(ListContract.ListItemEntry.COLUMN_UNIT_AMOUNT, Double.parseDouble(mUnitAmountView.getText().toString()));
        } else {
            values.put(ListContract.ListItemEntry.COLUMN_UNIT_AMOUNT, (Double) null);
        }
        if (!mTotalAmountView.getText().toString().isEmpty()) {
            values.put(ListContract.ListItemEntry.COLUMN_TOTAL_AMOUNT, Double.parseDouble(mTotalAmountView.getText().toString()));
        } else {
            values.put(ListContract.ListItemEntry.COLUMN_TOTAL_AMOUNT, (Double) null);
        }

        // If done
        if (mListItemDoneCheckBox.isEnabled()) {
            if (mListItemDoneCheckBox.isChecked()) {
                values.put(ListContract.ListItemEntry.COLUMN_DONE_TIME, time);
                values.put(ListContract.ListItemEntry.COLUMN_DONE, 1);
            } else {
                values.put(ListContract.ListItemEntry.COLUMN_DONE_TIME, (Long) null);
                values.put(ListContract.ListItemEntry.COLUMN_DONE, 0);
            }
        }
        return values;
    }
}
