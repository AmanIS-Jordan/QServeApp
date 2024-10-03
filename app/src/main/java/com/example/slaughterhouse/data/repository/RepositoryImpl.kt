package com.example.slaughterhouse.data.repository

import android.util.Log
import com.example.slaughterhouse.data.model.BranchesResponse
import com.example.slaughterhouse.data.model.CountersResponse
import com.example.slaughterhouse.data.model.HoldTicketsList
import com.example.slaughterhouse.data.model.LoginErrorResponse
import com.example.slaughterhouse.data.model.LoginSucessResponse
import com.example.slaughterhouse.data.model.OnHoldCountResponse
import com.example.slaughterhouse.data.model.PendingTicketsResponse
import com.example.slaughterhouse.data.model.SelectedTicketReponse
import com.example.slaughterhouse.data.remote.ApiInterface
import com.example.slaughterhouse.util.Resource
import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val apiInterface: ApiInterface) : Repository {

    override suspend fun login( username: String, password: String): Resource<LoginSucessResponse> {
        return try {
            val response = apiInterface.loginApi( username, password)
            Log.v("UserRepositoryImpl", "success")

            Resource.Success(response) // Assuming a successful response


        } catch (e: HttpException) {
            // Parse the error response body
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginErrorResponse::class.java)

            // Access the status code
            val statusCode = e.code()

            // Handle specific status codes
            when (statusCode) {

                404 -> Resource.Error(errorResponse.messageEn ?: "Resource not found.")
                401 -> Resource.Error(errorResponse.messageEn ?: "Invalid credentials.")
                500 -> Resource.Error(
                    errorResponse.messageEn ?: "Server error. Please try again later."
                )

                else -> Resource.Error(errorResponse.messageEn ?: "Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl", e.message.toString())

            Resource.Error(e.message ?: "An error occurred")

        }
    }

    override suspend fun getBranches(username: String): Resource<List<BranchesResponse>> {

        return try {
            val branchResponse = apiInterface.getBranches(username)
            Log.v("UserRepositoryImpl branch", "Raw response: $branchResponse") // Log raw response

            Resource.Success(branchResponse)

        } catch (e: HttpException) {
            Log.v("UserRepositoryImpl brach", e.code().toString())


            val statusCode = e.code()

            when (statusCode) {
                404 -> Resource.Error("Resource not found.")
                401 -> Resource.Error("Invalid credentials.")
                500 -> Resource.Error("Server error. Please try again later.")
                else -> Resource.Error("Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl brach", e.message.toString())
            Resource.Error(e.message ?: "An error occurred")

        }

    }

    override suspend fun getCounters(
        username: String,
        branchCode: String
    ): Resource<List<CountersResponse>> {
        return try {
            val countersResponse = apiInterface.getCounters(username, branchCode)
            Resource.Success(countersResponse)
        } catch (e: HttpException) {
            Log.v("UserRepositoryImpl counter", e.code().toString())


            val statusCode = e.code()

            when (statusCode) {
                404 -> Resource.Error("Resource not found.")
                401 -> Resource.Error("Invalid credentials.")
                500 -> Resource.Error("Server error. Please try again later.")
                else -> Resource.Error("Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl brach", e.message.toString())
            Resource.Error(e.message ?: "An error occurred")

        }
    }

    override suspend fun getHoldCount(
        counter: String,
        branchCode: String
    ): Resource<OnHoldCountResponse> {
        return try {
            val response = apiInterface.getHoldCount(counter, branchCode)

            Resource.Success(response)
        }catch (e: HttpException) {
            // Parse the error response body
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginErrorResponse::class.java)

            // Access the status code
            val statusCode = e.code()

            // Handle specific status codes
            when (statusCode) {

                404 -> Resource.Error(errorResponse.messageEn ?: "Resource not found.")
                401 -> Resource.Error(errorResponse.messageEn ?: "Invalid credentials.")
                500 -> Resource.Error(
                    errorResponse.messageEn ?: "Server error. Please try again later."
                )

                else -> Resource.Error(errorResponse.messageEn ?: "Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl", e.message.toString())

            Resource.Error(e.message ?: "An error occurred")

        }
    }

    override suspend fun getPendingTickets(
        counter: String,
        branchCode: String
    ): Resource<List<PendingTicketsResponse>> {
        return try {
            val tickedResponse = apiInterface.getPending(counter, branchCode)
            Resource.Success(tickedResponse)
        } catch (e: HttpException) {
            Log.v("UserRepositoryImpl counter", e.code().toString())


            val statusCode = e.code()

            when (statusCode) {
                404 -> Resource.Error("Resource not found.")
                401 -> Resource.Error("Invalid credentials.")
                500 -> Resource.Error("Server error. Please try again later.")
                else -> Resource.Error("Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl brach", e.message.toString())
            Resource.Error(e.message ?: "An error occurred")

        }
    }

    override suspend fun getHoldList(
        counter: String,
        branchCode: String
    ): Resource<MutableList<HoldTicketsList>> {
        return try {
            val holdTicketResponse = apiInterface.getHoldTicketsList(counter, branchCode)
            Resource.Success(holdTicketResponse)
        } catch (e: HttpException) {
            Log.v("UserRepositoryImpl counter", e.code().toString())


            val statusCode = e.code()

            when (statusCode) {
                404 -> Resource.Error("Resource not found.")
                401 -> Resource.Error("Invalid credentials.")
                500 -> Resource.Error("Server error. Please try again later.")
                else -> Resource.Error("Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl brach", e.message.toString())
            Resource.Error(e.message ?: "An error occurred")

        }
    }

    override suspend fun recallTicket(
        counter: String,
        userid: String,
        ticketNo: String,
        ticketId: String,
        refNo: String
    ): Resource<Response<Unit>> {
        return try {
            val recalTicket = apiInterface.recallTicket(counter, userid, ticketNo, ticketId, refNo)
            Resource.Success(recalTicket)

        } catch (e: HttpException) {
            Log.v("UserRepositoryImpl counter", e.code().toString())


            val statusCode = e.code()

            when (statusCode) {
                404 -> Resource.Error("Resource not found.")
                401 -> Resource.Error("Invalid credentials.")
                500 -> Resource.Error("Server error. Please try again later.")
                else -> Resource.Error("Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl brach", e.message.toString())
            Resource.Error(e.message ?: "An error occurred")

        }
    }

    override suspend fun proceedTicket(
        counter: String,
        branchCode: String,
        ticketNo: String,
        userid: String,
        ticketId: String
    ): Resource<Response<Unit>> {
        return try {
            val proceedTicket = apiInterface.proceedTicket(counter, branchCode, ticketNo, userid, ticketId)
            Resource.Success(proceedTicket)
        }catch (e: HttpException) {
            Log.v("UserRepositoryImpl counter", e.code().toString())


            val statusCode = e.code()

            when (statusCode) {
                404 -> Resource.Error("Resource not found.")
                401 -> Resource.Error("Invalid credentials.")
                500 -> Resource.Error("Server error. Please try again later.")
                else -> Resource.Error("Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl brach", e.message.toString())
            Resource.Error(e.message ?: "An error occurred")

        }

    }

    override suspend fun skipTicket(
        counter: String,
        branchCode: String,
        ticketNo: String,
        userid: String,
        ticketId: String
    ): Resource<Response<Unit>> {
        return try {
            val skipTicket = apiInterface.skipTicket(counter, branchCode, ticketNo, userid, ticketId)
            Resource.Success(skipTicket)
        }catch (e: HttpException) {
            Log.v("UserRepositoryImpl counter", e.code().toString())


            val statusCode = e.code()

            when (statusCode) {
                404 -> Resource.Error("Resource not found.")
                401 -> Resource.Error("Invalid credentials.")
                500 -> Resource.Error("Server error. Please try again later.")
                else -> Resource.Error("Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl brach", e.message.toString())
            Resource.Error(e.message ?: "An error occurred")

        }
    }

    override suspend fun holdTicket(
        counter: String,
        branchCode: String,
        ticketNo: String,
        userid: String,
        ticketId: String
    ): Resource<Response<Unit>> {
        return try {
            val holdTicket = apiInterface.holdTicket(counter, branchCode, ticketNo, userid, ticketId)
            Resource.Success(holdTicket)
        }catch (e: HttpException) {
            Log.v("UserRepositoryImpl counter", e.code().toString())


            val statusCode = e.code()

            when (statusCode) {
                404 -> Resource.Error("Resource not found.")
                401 -> Resource.Error("Invalid credentials.")
                500 -> Resource.Error("Server error. Please try again later.")
                else -> Resource.Error("Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl brach", e.message.toString())
            Resource.Error(e.message ?: "An error occurred")

        }
    }

    override suspend fun randomCall(
        counter: String,
        userid: String,
        ticketNo: String,
        ticketId: String,
        refNo: String,
        branchCode: String
    ): Resource<Response<Unit>> {
        return try {
            val holdTicket = apiInterface.randomCallTicket(counter, userid, ticketNo, ticketId, refNo, branchCode)
            Resource.Success(holdTicket)
        }catch (e: HttpException) {
            Log.v("UserRepositoryImpl counter", e.code().toString())


            val statusCode = e.code()

            when (statusCode) {
                404 -> Resource.Error("Resource not found.")
                401 -> Resource.Error("Invalid credentials.")
                500 -> Resource.Error("Server error. Please try again later.")
                else -> Resource.Error("Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl brach", e.message.toString())
            Resource.Error(e.message ?: "An error occurred")

        }    }

    override suspend fun getSelectedTicket(
        counter: String,
        branchCode: String
    ): Resource<PendingTicketsResponse> {
        return try {
            val selectedTicket = apiInterface.getSelectedTicket(counter, branchCode)
            Resource.Success(selectedTicket)
        } catch (e: HttpException) {
            // Parse the error response body
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginErrorResponse::class.java)

            // Access the status code
            val statusCode = e.code()

            // Handle specific status codes
            when (statusCode) {

                404 -> Resource.Error(errorResponse.messageEn ?: "Resource not found.")
                401 -> Resource.Error(errorResponse.messageEn ?: "Invalid credentials.")
                500 -> Resource.Error(
                    errorResponse.messageEn ?: "Server error. Please try again later."
                )

                else -> Resource.Error(errorResponse.messageEn ?: "Unknown error occurred")
            }

        } catch (e: Exception) {
            Log.v("UserRepositoryImpl", e.message.toString())

            Resource.Error(e.message ?: "An error occurred")
        }
    }
}



