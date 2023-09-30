package com.uts.homelab.view.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uts.homelab.R
import com.uts.homelab.databinding.FragmentSearchNurseBinding
import com.uts.homelab.network.dataclass.NurseWorkingAdapter
import com.uts.homelab.utils.dialog.InformationFragment
import com.uts.homelab.utils.dialog.ProgressFragment
import com.uts.homelab.utils.extension.toastMessage
import com.uts.homelab.view.adapter.AdapterNurseWorkingDay
import com.uts.homelab.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SearchNurseFragment : Fragment() {

    private lateinit var  binding:FragmentSearchNurseBinding
    private  val viewModel:AdminViewModel by activityViewModels()
    private val dialogProgress = ProgressFragment()
    private var informationFragment: InformationFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchNurseBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_hospital)


        viewModel.getAllNurseWorkingDay()
        setObserver()

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if(query!!.isEmpty()){
                    binding.rvNurses.adapter = AdapterNurseWorkingDay(viewModel.rvNurseWorkingAdapter.value!!)

                }else{
                    listNurseWorkingAdapter.clear()

                    viewModel.rvNurseWorkingAdapter.value!!.forEach {
                        val containsABC = it.name.contains(query, ignoreCase = true)
                        val lastName = it.lastName.contains(query, ignoreCase = true)

                        if (containsABC || lastName) {
                            listNurseWorkingAdapter.add(it)
                        }
                    }

                    if(listNurseWorkingAdapter.isEmpty()) {
                        toastMessage("NO SE ENCONTRARON RESULTADOS")
                    }else{
                        binding.rvNurses.adapter = AdapterNurseWorkingDay(listNurseWorkingAdapter)
                    }

                }



                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isEmpty()){
                    binding.rvNurses.adapter = AdapterNurseWorkingDay(viewModel.rvNurseWorkingAdapter.value!!)
                }
                return true
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }
    private var listNurseWorkingAdapter = arrayListOf<NurseWorkingAdapter>()

    private fun setObserver() {
        viewModel.isProgress.observe(viewLifecycleOwner){
            if(it == null ) return@observe

            if(it.first){
                if(dialogProgress.isVisible){
                    dialogProgress.dismiss()
                }
                dialogProgress.show(childFragmentManager,"Progress Fragment ${javaClass.name}")
            }else{
                if(dialogProgress.isVisible){
                    dialogProgress.dismiss()
                }
            }
        }

        viewModel.rvNurseWorkingAdapter.observe(viewLifecycleOwner){
            if(it == null ) return@observe
            binding.rvNurses.layoutManager = LinearLayoutManager(requireContext())
            binding.rvNurses.adapter = AdapterNurseWorkingDay(it)
        }

        viewModel.informationFragment.observe(viewLifecycleOwner){
            if (it == null) return@observe

            informationFragment = InformationFragment()

            when (it) {
                getString(R.string.is_nurse_add) -> {
                    informationFragment!!.getInstance(
                        getString(R.string.correct),
                        it
                    )
                }
                else -> {
                    informationFragment!!.getInstance(
                        getString(R.string.attention),
                        it
                    )
                }
            }

            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (informationFragment!!.isVisible || informationFragment!!.isAdded) {
                        informationFragment!!.dismiss()
                    }

                    if (it == getString(R.string.is_nurse_add)) {
                        clear()
                        CoroutineScope(Dispatchers.Main).launch {
                            findNavController().popBackStack()
                        }
                    }
                }
            }, 3000)

            informationFragment!!.showNow(
                childFragmentManager,
                "InformationFragment"
            )
        }
    }

    fun clear(){
        viewModel.informationFragment.value = null
        viewModel.rvNurseWorkingAdapter.value = null
    }

}