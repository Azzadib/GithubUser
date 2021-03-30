package com.adib.githubuser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.adib.githubuser.databinding.ActivityMainBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val list =  ArrayList<User>()

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvList.setHasFixedSize(true)
        searchUser()
    }

    private fun searchUser() {
        binding.search.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.search.clearFocus()
                binding.tvNotFound.visibility = View.INVISIBLE
                binding.progressbar.visibility = View.VISIBLE
                binding.rvList.visibility = View.INVISIBLE
                binding.tvOnStart.visibility = View.INVISIBLE

                if (query?.isEmpty() == true) {
                    binding.tvOnStart.visibility = View.VISIBLE
                    binding.tvNotFound.visibility = View.INVISIBLE
                    return true
                }
                else {
                    list.clear()
                    if (query != null) getUserSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                list.clear()
                binding.tvNotFound.visibility = View.INVISIBLE
                binding.tvOnStart.visibility = View.INVISIBLE
                if (newText != null) getUserSearch(newText)
                return false
            }
        })
    }

    private fun getUserDetail(id: String) {
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$id"
        client.get(url, object: AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                binding.progressbar.visibility = View.INVISIBLE
                val result = responseBody?.let { String(it) }
                Log.d(TAG, result)
                try {
                    val jsonObject = JSONObject(result)
                    val avatar: String = jsonObject.getString("avatar_url").toString()
                    val username: String = jsonObject.getString("login").toString()
                    val name: String = jsonObject.getString("name").toString()
                    val location: String = jsonObject.getString("location").toString()
                    val repository: Int = jsonObject.getInt("public_repos")
                    val company: String = jsonObject.getString("company").toString()
                    val followers: Int = jsonObject.getInt("followers")
                    val following: Int = jsonObject.getInt("following")

                    list.add(
                        User(
                        avatar,
                        username,
                        name,
                        location,
                        repository,
                        company,
                        followers,
                        following
                    ))
                    showRecyclerList()
                }
                catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding.progressbar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message + " DETAIL"}"
                }
                Log.e(TAG, errorMessage)
            }
        })
    }

    private fun getUserSearch(id: String) {
        binding.progressbar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/search/users?q=$id"
        client.get(url, object: AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                binding.progressbar.visibility = View.INVISIBLE
                val result = responseBody?.let { String(it) }
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONObject(result)
                    val item = jsonArray.getJSONArray("items")
                    if (item.length() < 1) binding.tvNotFound.visibility = View.VISIBLE
                    for (i in 0 until item.length()) {
                        val jsonObject = item.getJSONObject(i)
                        val username: String = jsonObject.getString("login")
                        getUserDetail(username)
                    }
                }
                catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding.progressbar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message + " GIT"}"
                }
                Log.e(TAG, errorMessage)
            }
        })
    }

    private fun showSelectedUser(user: User) {
        Log.d(TAG, "Open ${user.name?.substringBefore(" ")}'s profile")
        val userDetail = User(
            user.avatar,
            user.username,
            user.name,
            user.location,
            user.repository,
            user.company,
            user.followers,
            user.following
        )
        val intentDetail = Intent(this@MainActivity, DetailActivity::class.java)
        intentDetail.putExtra(DetailActivity.DETAIL_USER, userDetail)
        startActivity(intentDetail)
    }

    private fun showRecyclerList() {
        binding.rvList.visibility = View.VISIBLE
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val userAdapter = UserAdapter(list)
        binding.rvList.adapter = userAdapter

        userAdapter.setOnItemClickCallback(object: UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }
}

