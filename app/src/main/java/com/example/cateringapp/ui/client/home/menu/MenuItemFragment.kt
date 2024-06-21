package com.example.cateringapp.ui.client.home.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentMenuItemBinding
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.ui.client.orders.OrdersViewModel
import com.example.cateringapp.utils.loadImgUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuItemFragment : Fragment() {

    private val menuViewModel: MenuViewModel by activityViewModels()
    private val orderVM: OrdersViewModel by activityViewModels()

    private var _binding: FragmentMenuItemBinding? = null
    private val binding get() = _binding!!

    val args: MenuItemFragmentArgs by navArgs()


    private lateinit var menuItem: MenuItemRes
    private var itemCount: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuItemBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuItem = menuViewModel.getItemById(args.menuItemId)!!
        fragmentInit()
    }

    private fun fragmentInit() {
        loadImg(menuItem.imgUrl)
        binding.apply {
            tvName.text = menuItem.name
            tvDesc.text = menuItem.description
            tvMetrics.text = menuItem.metrics
            tvPrice.text = "${menuItem.price} грн"
        }
        setupOrderUI()
    }

    private fun setupOrderUI() {
        binding.btnOrder.setOnClickListener {
            onAddToOrderClick()
        }

        setTotalPrice( menuItem.price.toString())
        binding.btnPlus.setOnClickListener {
            countUpdate(itemCount + 1)
        }
        binding.btnMinus.setOnClickListener {
            countUpdate(itemCount - 1)
        }
    }

    //TODO BACKEND ITEM COUNT FIX NEEDED
    private fun onAddToOrderClick(){
        if (!orderVM.currentOrder.isLocal){
            addToOrderConfirmationDialog()
        }else {
            addToOrder()
        }
    }

    private fun addToOrder(){
        orderVM.addToOrder(menuItem, itemCount)
        (requireActivity() as MainActivity).setOrderBadge(1, true)
        findNavController().popBackStack()
    }





    private fun countUpdate(newCount: Int){
        if (newCount > 0){
            itemCount = newCount
            binding.tvCount.text = itemCount.toString()
            setTotalPrice((menuItem.price * itemCount).toString())
        }
    }

    private fun setTotalPrice(total: String){
        binding.tvTotalValue.text = total + " грн"

    }

    private fun loadImg(imgUrl: String) {
        loadImgUrl(imgUrl,binding.img, R.drawable.baseline_food_bank_24)
    }//TODO: std img

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun addToOrderConfirmationDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder
            .setMessage(getString(R.string.add_to_remote_order_message))
            .setTitle(getString(R.string.order_update))
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                addToOrder()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

}
