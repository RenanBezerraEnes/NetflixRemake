package co.renanbezerra.netflixremake

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.renanbezerra.netflixremake.model.Category
import co.renanbezerra.netflixremake.model.Movie

class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClickListener: (Int) -> Unit
): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categories = categories[position]
        holder.bind(categories)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category){
            val txtTitle:TextView = itemView.findViewById(R.id.txt_title)
            txtTitle.text = category.name

            val rvCategories: RecyclerView = itemView.findViewById(R.id.rv_category)
            rvCategories.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            rvCategories.adapter = MovieAdapter(category.movies, R.layout.movie_item, onItemClickListener)
        }
    }
}