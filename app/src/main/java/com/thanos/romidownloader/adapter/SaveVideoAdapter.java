package com.thanos.romidownloader.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thanos.romidownloader.R;
import com.thanos.romidownloader.databinding.VideoFileItemBinding;
import com.thanos.romidownloader.utils.VideoFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SaveVideoAdapter extends RecyclerView.Adapter<SaveVideoAdapter.SaveFileViewHolder>
{
	private File[] allFiles;
	private ArrayList<VideoFile> videoFileArrayList;
	private Context context;
	OnItemClickListener listener;


	public SaveVideoAdapter(File[] allFiles, ArrayList<VideoFile> videoFileArrayList, OnItemClickListener listener) {
		this.allFiles = allFiles;
		this.videoFileArrayList = videoFileArrayList;
		this.listener = listener;
	}
	public interface OnItemClickListener {
		void onItemClick(SaveFileViewHolder holder, int position, VideoFile videoFile);
	}
	
	public void remove(int position) {
		this.videoFileArrayList.remove(position);
		notifyItemRemoved(position);
	}
	
	public void update(List<VideoFile> data) {
		this.videoFileArrayList.clear();
		this.videoFileArrayList.addAll(data);
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public SaveFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		VideoFileItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_file_item, parent, false);
		context = parent.getContext();
		return new SaveFileViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(@NonNull SaveFileViewHolder holder, int position) {
		final VideoFile file = videoFileArrayList.get(position);
		holder.binding.tvTitle.setText(file.getFilename());
		
		
		if (file.getAddedDate() != null&&file.getDuration()!=null) {
			
			holder.binding.duration.setText(getDate(Long.parseLong(file.getAddedDate())) + " " + convertMillieToHMmSs(Long.parseLong(file.getDuration())));
		}
		Glide.with(context)
				.load(Uri.fromFile(new File(file.getPath())))
				.placeholder(R.drawable.video_play)
				.error(R.drawable.video_play)
				.into(holder.binding.img);
		
		long fileSize = (Long.parseLong(file.getTotalSize()) / 1024);
		float mbSize = fileSize / 1024;
		float gbSize = mbSize / 1024;
		if (gbSize > 1) {
			holder.binding.tvSize.setText(String.format("%.2f", gbSize).replace(".00", "") + " GB");
		} else if (mbSize > 1)
			holder.binding.tvSize.setText(String.format("%.2f", mbSize).replace(".00", "") + " MB");
		else
			holder.binding.tvSize.setText(fileSize + " KB");


		
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {

					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getPath()));
					intent.setDataAndType(Uri.parse(file.getPath()), "video/mp4");
					context.startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}

	public static String convertMillieToHMmSs(long millie) {
		long seconds = (millie / 1000);
		long second = seconds % 60;
		long minute = (seconds / 60) % 60;
		long hour = (seconds / (60 * 60)) % 24;
		
		String result = "";
		if (hour > 0) {
			return String.format("%02d:%02d:%02d", hour, minute, second);
		} else {
			return String.format("%02d:%02d", minute, second);
		}
		
	}
	
	private String getDate(long time) {
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		cal.setTimeInMillis(time * 1000);
		String date = DateFormat.format("dd-MM-yyyy", cal).toString();
		return date;
	}


	public void scanFile(File file) {
		Uri uri = Uri.fromFile(file);
		Intent scanFileIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
		context.sendBroadcast(scanFileIntent);
	}

	
	@Override
	public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver
													  observer) {
		super.unregisterAdapterDataObserver(observer);
	}
	
	@Override
	public int getItemCount() {
		return videoFileArrayList.size();
	}


	
	public class SaveFileViewHolder extends RecyclerView.ViewHolder {
		private VideoFileItemBinding binding;
		
		public SaveFileViewHolder(@NonNull VideoFileItemBinding itemBinding) {
			super(itemBinding.getRoot());
			binding = itemBinding;
		}
	}

}
