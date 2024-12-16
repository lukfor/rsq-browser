function populateRecentUploads() {
  const recentUploadsString = localStorage.getItem('recent_uploads');
  const recentUploadsMenu = $('#recentUploadsMenu');

  // Clear existing items except the static ones (divider and clear button)
  recentUploadsMenu.children().not('.dropdown-divider, #clearHistoryButton').remove();

  if (recentUploadsString) {
    const recentUploads = JSON.parse(recentUploadsString);

    if (recentUploads.length > 0) {
      recentUploads.slice(-10).forEach((job) => {
        var dateObj = new Date(job.createdOn);
        var formattedDate = `${dateObj.getFullYear()}-${String(dateObj.getMonth() + 1).padStart(2, '0')}-${String(dateObj.getDate()).padStart(2, '0')} ${String(dateObj.getHours()).padStart(2, '0')}:${String(dateObj.getMinutes()).padStart(2, '0')}:${String(dateObj.getSeconds()).padStart(2, '0')}`;

        const item = $('<a>', {
          class: 'dropdown-item',
          href: `{{baseUrl}}/reports/${job.id}`,
          html: `<i class="far fa-file"></i> Report ${job.id}<br><small class="text-muted">${formattedDate}</small>`
        });
        recentUploadsMenu.prepend(item);
      });
    } else {
      recentUploadsMenu.prepend('<span class="dropdown-item">No reports available</span>');
    }
  } else {
    recentUploadsMenu.prepend('<span class="dropdown-item">No reports available</span>');
  }
}

// Clear history function
function clearHistory() {
bootbox.confirm({
  title: "Clear History",
  message: "Are you sure you want to clear all history? This action cannot be undone.",
  buttons: {
    confirm: {
      label: 'Yes',
      className: 'btn-danger'
    },
    cancel: {
      label: 'No',
      className: 'btn-secondary'
    }
  },
  callback: function (result) {
    if (result) {
      localStorage.removeItem('recent_uploads');
      populateRecentUploads();
    }
  }
});
}

// Populate the dropdown dynamically when the user clicks on "History"
$('#historyDropdown').on('click', populateRecentUploads);

// Attach event listener to Clear History button
$('#recentUploadsMenu').on('click', '#clearHistoryButton', clearHistory);