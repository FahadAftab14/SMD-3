package com.example.taskflow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskflow.R;
import com.example.taskflow.adapters.AttachmentListAdapter;
import com.example.taskflow.data.AttachmentCatalog;
import com.example.taskflow.data.AttachmentLink;
import com.example.taskflow.data.CatalogItem;
import com.example.taskflow.data.Project;
import com.example.taskflow.ui.AttachmentPreviewDialog;
import com.example.taskflow.ui.CatalogPickerDialog;
import com.example.taskflow.ui.RemoveAttachmentDialog;
import com.example.taskflow.util.SimpleTextWatcher;

import java.util.ArrayList;
import java.util.Locale;

/**
 * The project's attachment links: search + All/Images/Text chips, preview,
 * add from the catalog and remove with typed confirmation. Chip counts always
 * refer to the complete collection, not the filtered list.
 */
public class FilesFragment extends ProjectTabFragment implements AttachmentListAdapter.Listener {

    private static final int TYPE_ALL = 0;
    private static final int TYPE_IMAGES = 1;
    private static final int TYPE_TEXT = 2;

    private static final String STATE_SEARCH = "files_search";
    private static final String STATE_TYPE = "files_type";

    private String search = "";
    private int typeFilter = TYPE_ALL;
    private final ArrayList<AttachmentLink> shownLinks = new ArrayList<>();
    private AttachmentListAdapter adapter;

    private EditText etSearch;
    private TextView chipAll;
    private TextView chipImages;
    private TextView chipText;
    private TextView tvShowing;
    private ListView listFiles;
    private View emptyNoAttachments;
    private View emptyNoMatch;

    public static FilesFragment newInstance(String projectId) {
        return withProjectId(new FilesFragment(), projectId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            search = savedInstanceState.getString(STATE_SEARCH, "");
            typeFilter = savedInstanceState.getInt(STATE_TYPE, TYPE_ALL);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SEARCH, search);
        outState.putInt(STATE_TYPE, typeFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etAttachmentSearch);
        chipAll = view.findViewById(R.id.chipAll);
        chipImages = view.findViewById(R.id.chipImages);
        chipText = view.findViewById(R.id.chipText);
        tvShowing = view.findViewById(R.id.tvShowingAttachments);
        listFiles = view.findViewById(R.id.listFiles);
        emptyNoAttachments = view.findViewById(R.id.emptyNoAttachments);
        emptyNoMatch = view.findViewById(R.id.emptyNoAttachmentMatch);

        adapter = new AttachmentListAdapter(requireContext(), shownLinks, this);
        listFiles.setAdapter(adapter);

        etSearch.setText(search);
        etSearch.addTextChangedListener(new SimpleTextWatcher(text -> {
            search = text;
            refreshContent();
        }));

        chipAll.setOnClickListener(v -> selectType(TYPE_ALL));
        chipImages.setOnClickListener(v -> selectType(TYPE_IMAGES));
        chipText.setOnClickListener(v -> selectType(TYPE_TEXT));

        view.findViewById(R.id.btnFilesAddAttachment).setOnClickListener(v -> showAddAttachmentDialog());
        view.findViewById(R.id.btnEmptyAddAttachment).setOnClickListener(v -> showAddAttachmentDialog());
        view.findViewById(R.id.btnEmptyResetAttachments).setOnClickListener(v -> resetFilters());

        onRefresh();
    }

    private void selectType(int type) {
        typeFilter = type;
        refreshContent();
    }

    private void resetFilters() {
        search = "";
        typeFilter = TYPE_ALL;
        if (etSearch != null) {
            etSearch.setText("");
        }
        refreshContent();
    }

    /** Single-choice catalog dialog: only unlinked items, Add disabled until one is selected. */
    private void showAddAttachmentDialog() {
        CatalogPickerDialog.show(requireContext(), "Add attachment", "Add",
                currentProject().getLinkedCatalogIds(), item -> {
                    currentProject().addAttachment(AttachmentCatalog.createLink(item));
                    host.onProjectDataChanged();
                });
    }

    @Override
    protected void onRefresh() {
        Project project = currentProject();
        String needle = search.trim().toLowerCase(Locale.ROOT);

        int total = project.getAttachments().size();
        int imageCount = 0;
        int textCount = 0;
        shownLinks.clear();

        // Links stay in the order they were added.
        for (AttachmentLink link : project.getAttachments()) {
            CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
            boolean isImage = item != null && item.isImage();
            if (isImage) {
                imageCount++;
            } else {
                textCount++;
            }
            boolean typeMatches = typeFilter == TYPE_ALL
                    || (typeFilter == TYPE_IMAGES && isImage)
                    || (typeFilter == TYPE_TEXT && !isImage);
            String name = item != null ? item.getDisplayName() : "";
            boolean searchMatches = needle.isEmpty() || name.toLowerCase(Locale.ROOT).contains(needle);
            if (typeMatches && searchMatches) {
                shownLinks.add(link);
            }
        }
        adapter.notifyDataSetChanged();

        chipAll.setText("All (" + total + ")");
        chipImages.setText("Images (" + imageCount + ")");
        chipText.setText("Text (" + textCount + ")");
        styleChip(chipAll, typeFilter == TYPE_ALL);
        styleChip(chipImages, typeFilter == TYPE_IMAGES);
        styleChip(chipText, typeFilter == TYPE_TEXT);

        tvShowing.setText("Showing " + shownLinks.size() + " of " + total + " attachments");

        if (total == 0) {
            listFiles.setVisibility(View.GONE);
            emptyNoAttachments.setVisibility(View.VISIBLE);
            emptyNoMatch.setVisibility(View.GONE);
        } else if (shownLinks.isEmpty()) {
            listFiles.setVisibility(View.GONE);
            emptyNoAttachments.setVisibility(View.GONE);
            emptyNoMatch.setVisibility(View.VISIBLE);
        } else {
            listFiles.setVisibility(View.VISIBLE);
            emptyNoAttachments.setVisibility(View.GONE);
            emptyNoMatch.setVisibility(View.GONE);
        }
    }

    private void styleChip(TextView chip, boolean selected) {
        chip.setBackgroundResource(selected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        chip.setTextColor(requireContext().getColor(selected ? R.color.card_white : R.color.text_primary));
    }

    @Override
    public void onPreview(AttachmentLink link) {
        CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
        AttachmentPreviewDialog.show(requireContext(), link, item);
    }

    @Override
    public void onRemove(AttachmentLink link) {
        CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
        String name = item != null ? item.getDisplayName() : "";
        final String linkId = link.getId();
        RemoveAttachmentDialog.show(requireContext(), name, () -> {
            currentProject().removeAttachment(linkId);
            host.onProjectDataChanged();
        });
    }
}
