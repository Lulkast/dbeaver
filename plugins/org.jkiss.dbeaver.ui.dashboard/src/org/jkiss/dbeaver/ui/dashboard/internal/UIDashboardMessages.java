/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2020 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ui.dashboard.internal;

import org.eclipse.osgi.util.NLS;

public class UIDashboardMessages extends NLS {
    public static final String BUNDLE_NAME = "org.jkiss.dbeaver.ui.dashboard.internal.UIDashboardMessages"; //$NON-NLS-1$

    public static String pref_page_dashboards_group_common;
    public static String pref_page_dashboards_open_separate_connection_label;
    public static String dashboard_add_dialog_add_dashboard_label;
    public static String dashboard_add_dialog_table_column_dashboard_name;
    public static String dashboard_add_dialog_table_column_dashboard_description;
    public static String dashboard_add_dialog_no_more_dashboards_message;
    public static String dashboard_add_dialog_manage_button;
    public static String dashboard_add_dialog_add_button;
    
    public static String dashboard_edit_dialog_dashboard_title;
    public static String dashboard_edit_dialog_read_only_message;
    public static String dashboard_edit_dialog_info_group_main_info_label;
    public static String dashboard_edit_dialog_info_group_id_label;
    public static String dashboard_edit_dialog_info_group_name_label;
    public static String dashboard_edit_dialog_info_group_database_label;
    public static String dashboard_edit_dialog_select_button;
    public static String dashboard_edit_dialog_data_type_label;
    public static String dashboard_edit_dialog_data_type_description;
    public static String dashboard_edit_dialog_calc_type_label;
    public static String dashboard_edit_dialog_calc_type_description;
    public static String dashboard_edit_dialog_value_type_label;
    public static String dashboard_edit_dialog_value_type_description;
    public static String dashboard_edit_dialog_interval_label;
    public static String dashboard_edit_dialog_interval_description;
    public static String dashboard_edit_dialog_fetch_type_label;
    public static String dashboard_edit_dialog_fetch_type_description;
    public static String dashboard_edit_dialog_description_label;
    public static String dashboard_edit_dialog_queries_label;
    public static String dashboard_edit_dialog_line_separator_info_label;
    public static String dashboard_edit_dialog_renderind_label;
    public static String dashboard_edit_dialog_default_view_label;
    public static String dashboard_edit_dialog_dashboard_view_label;
    public static String dashboard_edit_dialog_update_period_ms_label;
    public static String dashboard_edit_dialog_maximum_items_label;
    
    public static String dashboard_item_config_dialog_dashboard_title;
    public static String dashboard_item_config_dialog_dashboard_info_label;
    public static String dashboard_item_config_dialog_dashboard_name_label;
    public static String dashboard_item_config_dialog_dashboard_description_label;
    public static String dashboard_item_config_dialog_sql_queries_button;
    public static String dashboard_item_config_dialog_dashboard_read_queries_label;
    public static String dashboard_item_config_dialog_dashboard_update_label;
    public static String dashboard_item_config_dialog_dashboard_update_period_label;
    public static String dashboard_item_config_dialog_dashboard_maximum_items_label;
    public static String dashboard_item_config_dialog_dashboard_view_title;
    public static String dashboard_item_config_dialog_dashboard_view_label;
    public static String dashboard_item_config_dialog_dashboard_view_description;
    public static String dashboard_item_config_dialog_dashboard_show_legend_label;
    public static String dashboard_item_config_dialog_dashboard_show_legend_description;
    public static String dashboard_item_config_dialog_dashboard_show_grid_label;
    public static String dashboard_item_config_dialog_dashboard_show_grid_description;
    public static String dashboard_item_config_dialog_dashboard_show_domain_axis_label;
    public static String dashboard_item_config_dialog_dashboard_show_domain_axis_description;
    public static String dashboard_item_config_dialog_dashboard_show_range_axis_label;
    public static String dashboard_item_config_dialog_dashboard_show_range_axis_description;
    public static String dashboard_item_config_dialog_configuration_label;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, UIDashboardMessages.class);
    }

    private UIDashboardMessages() {
    }
}
