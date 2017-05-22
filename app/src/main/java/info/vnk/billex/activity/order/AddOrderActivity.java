package info.vnk.billex.activity.order;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.vnk.billex.R;
import info.vnk.billex.adapter.order.AddOrderListAdapter;
import info.vnk.billex.adapter.order.listener.OrderListener;
import info.vnk.billex.base.BaseActivity;
import info.vnk.billex.custom.fullScreenSearch.FullScreenSearch;
import info.vnk.billex.custom.fullScreenSearch.listener.CustomListener;
import info.vnk.billex.custom.fullScreenSearch.model.SearchModel;
import info.vnk.billex.model.customer.CustomerModel;
import info.vnk.billex.model.customer.CustomerResultModel;
import info.vnk.billex.model.order.PostMainOrderModel;
import info.vnk.billex.model.order.PostOrderModel;
import info.vnk.billex.model.order.PostOrderResultModel;
import info.vnk.billex.model.product.GetProductModel;
import info.vnk.billex.model.product.GetProductResultModel;
import info.vnk.billex.model.product.PostProductModel;
import info.vnk.billex.model.product.ProductModel;
import info.vnk.billex.model.product.ProductResultModel;
import info.vnk.billex.network.ApiClient;
import info.vnk.billex.network.ApiInterface;
import info.vnk.billex.utilities.Constants;
import info.vnk.billex.utilities.General;
import me.drakeet.materialdialog.MaterialDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddOrderActivity extends BaseActivity {

    private static final int KEY_CUSTOMER = 1;
    private static final int KEY_PRODUCT = 2;
    private Context context;
    private FloatingActionButton btnAddFab;
    private FullScreenSearch fullScreenSearch;
    private List<PostProductModel> postProductModel = new ArrayList<>();
    private AddOrderListAdapter adapter;
    private SearchModel seletedCustomer;
    private List<ProductModel> mProductList;
    private List<CustomerModel> mCustomerList;
    private Toolbar toolbar;
    private EditText dateOfDelivery, dateOfOrder, customerText;
    private static final String TAG = "AddOrderActivity";
    private MaterialDialog mMaterialDialog;
    private String customerName, customerId, orderId, dod, doo;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);
        context = this;
        setPreference(context);
        toolbar = setToolbar();
        initToolbar();
        init();
        getIntentData();
    }

    private void getIntentData() {
        if(getIntent().hasExtra(Constants.IS_EDIT)) {
            isEdit = getIntent().getExtras().getBoolean(Constants.IS_EDIT);
            customerName = getIntent().getExtras().getString(Constants.CUSTOMER_NAME);
            customerId = getIntent().getExtras().getString(Constants.CUSTOMER_ID);
            orderId = getIntent().getExtras().getString(Constants.ORDER_ID);
            dod = getIntent().getExtras().getString(Constants.DOD);
            doo = getIntent().getExtras().getString(Constants.DOO);

            customerText.setText(customerName);
            dateOfDelivery.setText(General.DateFormatterMtoY(dod));
            dateOfOrder.setText(General.DateFormatterMtoY(doo));
            getOrderProduct();
        }
    }

    public void initToolbar() {
        TextView placeOrder = (TextView) findViewById(R.id.tv_right);
        placeOrder.setVisibility(View.VISIBLE);
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customerText.getText().length() <= 0){
                    return;
                } else if (dateOfOrder.getText().length() <= 0) {
                    return;
                } else if (dateOfDelivery.getText().length() <= 0){
                    return;
                } else if(postProductModel.size() <= 0){
                    return;
                } else {
                    setOrder();
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void init() {

        View viewOrder = findViewById(R.id.content_order_layout);
        customerText = (EditText) viewOrder.findViewById(R.id.et_select_customer);
        dateOfOrder = (EditText) viewOrder.findViewById(R.id.et_date_of_order);
        dateOfDelivery = (EditText) viewOrder.findViewById(R.id.et_date_of_delivery);
        Calendar mCalendar = Calendar.getInstance();
        dateOfOrder.setText(General.setDate(mCalendar));
        customerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFullScreenSearchVisible(true);
                getCustomerList();
            }
        });
        dateOfOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                General.setEditText(dateOfOrder);
                General.setCalendar(context);
            }
        });
        dateOfDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                General.setEditText(dateOfDelivery);
                General.setCalendar(context);
            }
        });

        btnAddFab = (FloatingActionButton) findViewById(R.id.fab_add);

        fullScreenSearch = (FullScreenSearch) viewOrder.findViewById(R.id.custom_search);
        fullScreenSearch.setListener(new CustomListener() {
            @Override
            public void onItemSelectedSearch(int key, SearchModel productModel) {
                setFullScreenSearchVisible(false);

                if(key == KEY_PRODUCT) {
                    btnAddFab.setVisibility(View.VISIBLE);
                    for (ProductModel data : mProductList) {
                        if(data.getPdtId() == productModel.getId()) {
                            PostProductModel model = new PostProductModel();
                            model.setPdt_id(data.getPdtId());
                            model.setPdt_name(data.getPdtName());
                            model.setPdt_qty("" + ProductModel.DEFAULT_QUANTITY);
                            model.setPdt_mrp(data.getMrp());
                            model.setPdt_price(data.getPrice1());
                            model.setPdt_tax(data.getAmount_tax());
                            model.setPdt_discount("" + ProductModel.DEFAULT_DISCOUNT);
                            postProductModel.add(model);
                            //Collections.reverse(postProductModel);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else if(key == KEY_CUSTOMER) {
                    setSeletedCustomer(productModel);
                    customerText.setText("" + productModel.getName());
                }
            }
        });
        RecyclerView recyclerOrder = (RecyclerView) viewOrder.findViewById(R.id.rv_order);
        recyclerOrder.setLayoutManager(new LinearLayoutManager(context));
        //Adapter
        adapter = new AddOrderListAdapter(getPostProductModel(), context);
        adapter.setListener(new OrderListener() {
            @Override
            public void plusClick(int position) {
                try {
                    int dataValue = Integer.parseInt(postProductModel.get(position).getPdt_qty());
                    dataValue++;
                    postProductModel.get(position).setPdt_qty("" + dataValue);
                    adapter.notifyDataSetChanged();
                } catch (NumberFormatException e) {
                    int dataValue = 1;
                    dataValue++;
                    postProductModel.get(position).setPdt_qty("" + dataValue);
                    adapter.notifyDataSetChanged();
                } catch (Exception e ){
                    e.printStackTrace();
                }
            }

            @Override
            public void minusClick(int position) {
                try {
                    int dataValue = Integer.parseInt(postProductModel.get(position).getPdt_qty());
                    if (dataValue > 1) {
                        dataValue--;
                        postProductModel.get(position).setPdt_qty("" + dataValue);
                    }
                    adapter.notifyDataSetChanged();
                } catch (NumberFormatException e) {
                    int dataValue = 1;
                    if (dataValue > 1) {
                        dataValue--;
                        postProductModel.get(position).setPdt_qty("" + dataValue);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void deleteClick(int position) {
                setDeleteConfirm(context, position);
            }

            @Override
            public Long quantityClick(final int position, final Long quantity){
                long data = 0;
                final EditText contentView = new EditText(context);
                contentView.setText("" + quantity);
                mMaterialDialog = new MaterialDialog(context).setView(contentView)
                        .setTitle(R.string.order_title)
                        .setMessage(R.string.enter_quantity)
                        .setPositiveButton(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.v(TAG,"" + contentView.getText());
                                postProductModel.get(position).setPdt_qty("" + contentView.getText());
                                adapter.notifyDataSetChanged();
                                mMaterialDialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
                return data;
            }

            @Override
            public void discountAdded(final int position, String discount) {
                final EditText contentView = new EditText(context);
                contentView.setText("" + discount);
                contentView.setFocusable(true);
                mMaterialDialog = new MaterialDialog(context).setView(contentView)
                        .setTitle(R.string.order_title)
                        .setMessage(R.string.enter_discount)
                        .setPositiveButton(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.v(TAG,"" + contentView.getText());
                                postProductModel.get(position).setPdt_discount("" + contentView.getText());
                                adapter.notifyDataSetChanged();
                                mMaterialDialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();                //adapter.notifyDataSetChanged();
            }
        });
        recyclerOrder.setAdapter(adapter);


        btnAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                setFullScreenSearchVisible(true);
                btnAddFab.setVisibility(View.GONE);
                getProductList();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");
        if(fullScreenSearch.getVisibility() == View.VISIBLE){
            setFullScreenSearchVisible(false);
        } else {
            if(postProductModel.size() != 0) {
                setDialogOkCancel(context);
            } else {
                finish();
            }
        }
    }

    public void setDeleteConfirm(Context context, final int index){
        mMaterialDialog = new MaterialDialog(context)
                .setTitle(R.string.order_title)
                .setMessage(R.string.order_delete)
                .setPositiveButton(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postProductModel.remove(index);
                        adapter.notifyDataSetChanged();
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        return;
                    }
                });
        mMaterialDialog.show();
    }

    public void setDialogOkCancel(Context context){
         mMaterialDialog = new MaterialDialog(context)
                .setTitle(R.string.order_title)
                .setMessage(R.string.order_message)
                .setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

    // call api to fetch data
    private void getProductList() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ProductResultModel> call = apiService.getProduct();
        call.enqueue(new Callback<ProductResultModel>() {
            @Override
            public void onResponse(Call<ProductResultModel> call, Response<ProductResultModel> response) {
                mProductList = response.body().getResult();
                fullScreenSearch.setSearchAdapter(context, KEY_PRODUCT, getProduct());
            }

            @Override
            public void onFailure(Call<ProductResultModel> call, Throwable t) {
                Toast.makeText(context, "error" + t.getLocalizedMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    private void getOrderProduct() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetProductResultModel> call = apiService.getOrderProduct(orderId);
        call.enqueue(new Callback<GetProductResultModel>() {
            @Override
            public void onResponse(Call<GetProductResultModel> call, Response<GetProductResultModel> response) {
                for(GetProductModel getProductModel :response.body().getResult()) {
                    PostProductModel model = new PostProductModel();
                    model.setPdt_id(getProductModel.getPdt_id());
                    model.setPdt_name(getProductModel.getPdt_name());
                    model.setPdt_qty("" + getProductModel.getPdt_qty());
                    model.setPdt_mrp(getProductModel.getPdt_mrp());
                    model.setPdt_tax(getProductModel.getPdt_tax_price());
                    model.setPdt_price(getProductModel.getPdt_price());
                    model.setPdt_discount("" + getProductModel.getPdt_discount());
                    postProductModel.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<GetProductResultModel> call, Throwable t) {
                Toast.makeText(context, "error" + t.getLocalizedMessage(), Toast.LENGTH_LONG);
            }
        });
    }


    // call api to fetch data
    private void getCustomerList() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerResultModel> call = apiService.getCustomer();
        call.enqueue(new Callback<CustomerResultModel>() {
            @Override
            public void onResponse(Call<CustomerResultModel> call, Response<CustomerResultModel> response) {
                mCustomerList = response.body().getResult();
                fullScreenSearch.setSearchAdapter(context, KEY_CUSTOMER, getCustomer());
            }

            @Override
            public void onFailure(Call<CustomerResultModel> call, Throwable t) {
                Toast.makeText(context, "error" + t.getLocalizedMessage(), Toast.LENGTH_LONG);
            }
        });

    }

    public List<SearchModel> getProduct(){
        List<SearchModel> list = new ArrayList<>();
        for (ProductModel data : mProductList) {
            SearchModel searchModel = new SearchModel();
            searchModel.setId(data.getPdtId());
            searchModel.setName(data.getPdtName());
            searchModel.setCode(data.getPdtCode());
            list.add(searchModel);
        }
        return list;
    }

    public List<SearchModel> getCustomer(){
        List<SearchModel> list = new ArrayList<>();
        for (CustomerModel data : mCustomerList) {
            SearchModel searchModel = new SearchModel();
            searchModel.setId(data.getId());
            searchModel.setName(data.getCustName());
            searchModel.setCode(data.getCustMobile());
            list.add(searchModel);
        }
        return list;
    }

    public PostMainOrderModel postOrder(){
        PostOrderModel orderModel = new PostOrderModel();
        if(isEdit){
            orderModel.setOrderId(orderId);
            orderModel.setCustomerId(Integer.parseInt(customerId));
        } else {
            orderModel.setCustomerId(getSeletedCustomer().getId());
        }
        orderModel.setDateOfDelivery(General.DateFormatter(dateOfDelivery.getText().toString()));
        orderModel.setDateOfOrder(General.DateFormatter(dateOfOrder.getText().toString()));
        orderModel.setStaffId(preferencesManager.getString(Constants.mUserId));
        orderModel.setListProduct(postProductModel);
        PostMainOrderModel orderMain = new PostMainOrderModel();
        orderMain.setModel(orderModel);
        return orderMain;
    }

    // call api to post data
    private void setOrder() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PostOrderResultModel> call = null;
        if(isEdit) {
            call = apiService.editOrder(postOrder());
        } else {
            call = apiService.postOrder(postOrder());
        }
        call.enqueue(new Callback<PostOrderResultModel>() {
            @Override
            public void onResponse(Call<PostOrderResultModel> call, Response<PostOrderResultModel> response) {
                //response.body().getMessage();
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<PostOrderResultModel> call, Throwable t) {
                Toast.makeText(context, "error" + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void setFullScreenSearchVisible(boolean value){
        if (value){
            fullScreenSearch.setVisibility(View.VISIBLE);
            fullScreenSearch.setVisibleRecycler(View.GONE);
            btnAddFab.setVisibility(View.GONE);
        } else {
            fullScreenSearch.setVisibility(View.GONE);
            btnAddFab.setVisibility(View.VISIBLE);
        }
    }

    public List<PostProductModel> getPostProductModel() {
        return postProductModel;
    }

    public void setPostProductModel(List<PostProductModel> postProductModel) {
        this.postProductModel = postProductModel;
    }

    public SearchModel getSeletedCustomer() {
        return seletedCustomer;
    }

    public void setSeletedCustomer(SearchModel seletedCustomer) {
        this.seletedCustomer = seletedCustomer;
    }
}

