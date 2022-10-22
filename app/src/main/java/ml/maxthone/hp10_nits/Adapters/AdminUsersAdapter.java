package ml.maxthone.hp10_nits.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.maxthone.hp10_nits.Admin.UserDetailsActivity;
import ml.maxthone.hp10_nits.Models.ModelUser;
import ml.maxthone.hp10_nits.R;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.ViewHolder>{
    private final Context context;
    private final List<ModelUser> users;

    public AdminUsersAdapter(Context context, List<ModelUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_user_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView role;
        private final CircleImageView profileImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            role = itemView.findViewById(R.id.user_role);
            profileImage = itemView.findViewById(R.id.user_icon);
        }

        public void setData(ModelUser user) {
            name.setText(user.getName());
            role.setText(user.getRole());
            if(!user.getProfileImage().equalsIgnoreCase("default")){
                Glide.with(context).load(user.getProfileImage()).into(profileImage);
            }
            itemView.setOnClickListener(view -> {
                Intent i =  new Intent(context, UserDetailsActivity.class);
                i.putExtra("id", user.getUid());
                i.putExtra("admin", true);
                context.startActivity(i);
            });
        }
    }
}
