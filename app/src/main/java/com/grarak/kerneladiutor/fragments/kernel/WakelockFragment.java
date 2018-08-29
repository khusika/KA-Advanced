/*
 * Copyright (C) 2018-2019 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of SmartPack Kernel Manager, which is heavily modified version of Kernel Adiutor,
 * originally developed by Willi Ye <williye97@gmail.com>
 *
 * Both SmartPack Kernel Manager & Kernel Adiutor are free softwares: you can redistribute it 
 * and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kernel Adiutor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kernel Adiutor. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.grarak.kerneladiutor.fragments.kernel;

import com.grarak.kerneladiutor.R;
import com.grarak.kerneladiutor.fragments.ApplyOnBootFragment;
import com.grarak.kerneladiutor.fragments.recyclerview.RecyclerViewFragment;
import com.grarak.kerneladiutor.utils.Utils;
import com.grarak.kerneladiutor.utils.kernel.misc.Wakelocks;
import com.grarak.kerneladiutor.utils.kernel.misc.WakeLockInfo;
import com.grarak.kerneladiutor.views.recyclerview.CardView;
import com.grarak.kerneladiutor.views.recyclerview.DescriptionView;
import com.grarak.kerneladiutor.views.recyclerview.RecyclerViewItem;
import com.grarak.kerneladiutor.views.recyclerview.SeekBarView;
import com.grarak.kerneladiutor.views.recyclerview.SelectView;
import com.grarak.kerneladiutor.views.recyclerview.SwitchView;
import com.grarak.kerneladiutor.views.recyclerview.TitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Originally authored by Morogoku <morogoku@hotmail.com>
 *
 * Modified by sunilpaulmathew <sunil.kde@gmail.com>
 */

public class WakelockFragment extends RecyclerViewFragment {

    private List<CardView> mWakeCard = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        addViewPagerFragment(ApplyOnBootFragment.newInstance(this));
    }

    @Override
    protected void addItems(List<RecyclerViewItem> items) {

        if (Wakelocks.boefflawlsupported()){
            boefflaWakelockInit(items);
        }
    }

    private void boefflaWakelockInit(List<RecyclerViewItem> items){
        mWakeCard.clear();

        DescriptionView warning= new DescriptionView();
        warning.setTitle(getString(R.string.warning));
        warning.setSummary(getString(R.string.wakelock_summary));
        items.add(warning);

        DescriptionView boefflawl = new DescriptionView();
        boefflawl.setTitle(getString(R.string.boeffla_wakelock));
        boefflawl.setSummary("Version: " + Wakelocks.getboefflawlVersion() + "\n" + getString(R.string.boeffla_wakelock_summary));
        items.add(boefflawl);

        SelectView borfflawlorder = new SelectView();
        borfflawlorder.setTitle(getString(R.string.wkl_order));
        borfflawlorder.setSummary(getString(R.string.wkl_order_summary));
        borfflawlorder.setItems(Arrays.asList(getResources().getStringArray(R.array.b_wakelocks_oder)));
        borfflawlorder.setItem(Wakelocks.getWakelockOrder());
        borfflawlorder.setOnItemSelected(new SelectView.OnItemSelected() {
            @Override
            public void onItemSelected(SelectView selectView, int position, String item) {
                Wakelocks.setWakelockOrder(position);
                bwCardReload();
            }
        });
        items.add(borfflawlorder);

        List<WakeLockInfo> wakelocksinfo = Wakelocks.getWakelockInfo();

        CardView cardViewB = new CardView(getActivity());
        String titleB = getString(R.string.wkl_blocked);
        grxbwCardInit(cardViewB, titleB, wakelocksinfo, false);
        mWakeCard.add(cardViewB);

        CardView cardViewA = new CardView(getActivity());
        String titleA = getString(R.string.wkl_allowed);
        CardView cardA = new CardView(getActivity());
        grxbwCardInit(cardViewA, titleA, wakelocksinfo, true);
        mWakeCard.add(cardViewA);

        items.addAll(mWakeCard);
    }

    private void grxbwCardInit(CardView card, String title, List<WakeLockInfo> wakelocksinfo, Boolean state){
        card.clearItems();
        card.setTitle(title);

        for(WakeLockInfo wakeLockInfo : wakelocksinfo){
             if(wakeLockInfo.wState == state) {
                 final String name = wakeLockInfo.wName;
                String wakeup = String.valueOf(wakeLockInfo.wWakeups);
                String time = String.valueOf(wakeLockInfo.wTime / 1000);
                time = Utils.sToString(Utils.strToLong(time));
                 SwitchView sw = new SwitchView();
                sw.setTitle(name);
                sw.setSummary(getString(R.string.wkl_total_time) + ": " + time + "\n" +
                        getString(R.string.wkl_wakep_count) + ": " + wakeup);
                sw.setChecked(wakeLockInfo.wState);
                sw.addOnSwitchListener((switchView, isChecked) -> {
                    if (isChecked) {
                        Wakelocks.setWakelockAllowed(name, getActivity());
                    } else {
                        Wakelocks.setWakelockBlocked(name, getActivity());
                    }
                    getHandler().postDelayed(this::bwCardReload, 250);
                });
                 card.addItem(sw);
            }
        }
    }

    private void bwCardReload() {

	List<WakeLockInfo> wakelocksinfo = Wakelocks.getWakelockInfo();
        String titleB = getString(R.string.wkl_blocked);
        grxbwCardInit(mWakeCard.get(0), titleB, wakelocksinfo, false);

        String titleA = getString(R.string.wkl_allowed);
        grxbwCardInit(mWakeCard.get(1), titleA, wakelocksinfo, true);

    }

}
