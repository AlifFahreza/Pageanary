package com.pagenary.question;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {


    private Activity mCtx;
    private List<Product> productList;

    public ProductsAdapter(Activity mCtx, List<Product> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.listproduct, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        final Product product = productList.get(position);

        Glide.with(mCtx)
                .load("http://pageanary.000webhostapp.com/imageberanda/" + product.getGambar())
                .into(holder.imageView);

        holder.textViewTitle.setText(product.getNama());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx.getApplicationContext(), MainActivity.class);
                intent.putExtra("nama", product.getNama());
                intent.putExtra("id_kategory", product.getId_kategory());
                mCtx.startActivity(intent);
                mCtx.overridePendingTransition(R.anim.leftanimation,
                        R.anim.rightanimation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        ImageView imageView;
        View view;

        public ProductViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            textViewTitle = itemView.findViewById(R.id.namaKategori);
            imageView = itemView.findViewById(R.id.imgKategori);
        }
    }
}