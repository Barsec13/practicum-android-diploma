package ru.practicum.android.diploma.favourite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavouriteBinding
import ru.practicum.android.diploma.details.presentation.ui.VacancyFragment
import ru.practicum.android.diploma.favourite.presentation.models.FavoriteStateInterface
import ru.practicum.android.diploma.favourite.presentation.models.GetFavouriteVacancyInfoState
import ru.practicum.android.diploma.favourite.presentation.viewvodel.FavouriteViewModel
import ru.practicum.android.diploma.search.domain.models.Vacancy
import ru.practicum.android.diploma.util.BindingFragment
import ru.practicum.android.diploma.util.adapter.VacancyAdapter
import ru.practicum.android.diploma.util.debounce

class FragmentFavourite : BindingFragment<FragmentFavouriteBinding>() {

    private lateinit var vacancyAdapter: VacancyAdapter
    private lateinit var confirmDialogDeleteFavouriteVacancy: MaterialAlertDialogBuilder
    private lateinit var onFavouriteVacancyClickDebounce: (Vacancy) -> Unit

    private val favouriteViewModel: FavouriteViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentFavouriteBinding {
        return FragmentFavouriteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()

        setListeners()

        favouriteViewModel.observeStateFavourite().observe(viewLifecycleOwner) { state ->
            renderStateFavouriteVacancies(state)
        }

        favouriteViewModel.observeStateGetVacancyInfo().observe(viewLifecycleOwner) { state ->
            renderStateGetInfoVacancy(state)
        }

        favouriteViewModel.showFavouriteVacancies()
    }

    override fun onPause() {
        super.onPause()
        favouriteViewModel.pause()
    }


    private fun initAdapter() {
        vacancyAdapter = VacancyAdapter(ArrayList<Vacancy>())
        binding.recyclerView.adapter = vacancyAdapter
    }

    private fun setListeners() {

        onFavouriteVacancyClickDebounce =
            debounce<Vacancy>(CLICK_DEBOUNCE_DELAY_MILLIS, lifecycleScope, false) { vacancy ->
                favouriteViewModel.getFavouriteVacancyInfo(vacancy.id)
            }

        vacancyAdapter.itemClickListener = { position, vacancy ->
            onFavouriteVacancyClickDebounce(vacancy)
        }

        vacancyAdapter.itemLongClickListener = { position, vacancy ->
            showConfirmDialog(vacancy)
        }
    }

    private fun renderStateFavouriteVacancies(favoriteStateInterface: FavoriteStateInterface?) {

        if (favoriteStateInterface == null) return

        when (favoriteStateInterface) {
            is FavoriteStateInterface.FavoriteVacanciesIsEmpty -> showPlaceHolder()
            is FavoriteStateInterface.FavoriteVacancies ->
                showFavoriteVacancies(favoriteStateInterface.favoriteVacancies)
        }
    }

    private fun renderStateGetInfoVacancy(
        favouriteVacanciesInfo: GetFavouriteVacancyInfoState?) {

        if (favouriteVacanciesInfo == null) return

        when(favouriteVacanciesInfo){
            is GetFavouriteVacancyInfoState.FavoriteVacanciesInfoIsEmpty -> return
            is GetFavouriteVacancyInfoState.FavoriteVacanciesInfo
            -> sendToDetail(favouriteVacanciesInfo.vacancy)
        }
    }

    private fun showPlaceHolder() {
        binding.placeholderFavourite.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        vacancyAdapter.setVacancies(null)
    }

    private fun showFavoriteVacancies(vacancies: List<Vacancy>) {
        binding.placeholderFavourite.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        vacancyAdapter.setVacancies(vacancies)
    }

    private fun showConfirmDialog(vacancy: Vacancy){
        confirmDialogDeleteFavouriteVacancy = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_vacancy)
            .setMessage("")
            .setPositiveButton(R.string.yes) { dialog, which ->
                favouriteViewModel.deleteVacancy(vacancy)
            }
            .setNegativeButton(R.string.no) { dialog, which -> }

        confirmDialogDeleteFavouriteVacancy.show()
    }

    private fun sendToDetail(vacancy: Vacancy) {
        findNavController().navigate(
            R.id.action_fragmentFavourite_to_vacancyFragment,
            VacancyFragment.createArgs(Gson().toJson(vacancy))
        )
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}