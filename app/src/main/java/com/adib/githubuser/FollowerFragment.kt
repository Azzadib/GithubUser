package com.adib.githubuser

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class FollowerFragment : Fragment() {

    private val listFollower = ArrayList<User>()
    private lateinit var adapter: FollowerAdapter
    private lateinit var pbFollower: ProgressBar
    private lateinit var rvFollower: RecyclerView
    private lateinit var tvNoFollower: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_follower, container, false)
    }

    companion object {
        private val TAG = FollowerFragment::class.java.simpleName
       const val DETAIL_USER = "detail_user"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FollowerAdapter(listFollower)
        pbFollower = view.findViewById(R.id.pb_follower)
        rvFollower = view.findViewById(R.id.rv_follower)
        tvNoFollower = view.findViewById(R.id.tv_no_follower)

        listFollower.clear()
        val follower = activity!!.intent.getParcelableExtra(DETAIL_USER) as User
        getUser(follower.username.toString())
    }

    private fun getUser(id: String) {
        pbFollower.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$id/followers"
        client.get(url, object: AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                val result = responseBody?.let { String(it) }
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONArray(result)
                    if (jsonArray.length() < 1) {
                        tvNoFollower.visibility = View.VISIBLE
                        pbFollower.visibility = View.INVISIBLE
                    }
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
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
                pbFollower.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Log.e(TAG, errorMessage)
            }
        })
    }

    private fun getUserDetail(id: String) {
        pbFollower.visibility = View.VISIBLE
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

                    listFollower.add(
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
                pbFollower.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Log.e(TAG, errorMessage)
            }
        })
    }

    private fun showRecyclerList() {
        rvFollower.visibility = View.VISIBLE
        rvFollower.layoutManager = LinearLayoutManager(activity)
        val followerAdapter = FollowerAdapter(listFollower)
        rvFollower.adapter = followerAdapter

        followerAdapter.setOnItemClickCallback(object:
        FollowerAdapter.OnItemClickCallback, FollowingAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {}
        })
        pbFollower.visibility = View.INVISIBLE
    }
}