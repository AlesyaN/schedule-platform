package ru.itis.scheduleplatform.io;

import com.google.common.collect.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import ru.itis.scheduleplatform.enums.DayOfWeek;
import ru.itis.scheduleplatform.models.Class;
import ru.itis.scheduleplatform.models.Group;
import ru.itis.scheduleplatform.models.ScheduleCell;
import ru.itis.scheduleplatform.models.TimeSlot;
import ru.itis.scheduleplatform.repositories.GroupRepository;
import ru.itis.scheduleplatform.repositories.TimeSlotRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class XlsxWriter {

    private String outputPath;
    private static final String SHEET_NAME = "Расписание";
    private static final int TOP_OFFSET = 2;
    private XSSFSheet xlsxSheet;

    private GroupRepository groupRepository;
    private TimeSlotRepository timeSlotRepository;

    public XlsxWriter(GroupRepository groupRepository, TimeSlotRepository timeSlotRepository) {
        this.groupRepository = groupRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    public void exportScheduleToFile(Table<ScheduleCell, Group, Class> schedule, String outputPath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        xlsxSheet = workbook.createSheet(SHEET_NAME);
        this.outputPath = outputPath;
        createTemplate();
        fillSchedule(schedule);
        autoSize();
        saveToFile();
    }

    private void createTemplate() {
        templateDaysOfWeek();
        templateTimeSlots();
        templateGroups();
    }

    private void templateDaysOfWeek() {
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        for (int i = 0; i < DayOfWeek.values().length; i++) {
            int rowNum = i * timeSlots.size() + TOP_OFFSET + 1;
            Row row = xlsxSheet.getRow(rowNum);
            if (row == null) {
                row = xlsxSheet.createRow(rowNum);
            }
            row.createCell(0).setCellValue(DayOfWeek.values()[i].toString());
            CellRangeAddress mergedCell = new CellRangeAddress(row.getRowNum(),
                    row.getRowNum() + timeSlots.size() - 1,
                    0,
                    0);
            xlsxSheet.addMergedRegion(mergedCell);

            RegionUtil.setBorderBottom(BorderStyle.MEDIUM, mergedCell, xlsxSheet);
            RegionUtil.setBorderTop(BorderStyle.MEDIUM, mergedCell, xlsxSheet);
            RegionUtil.setBorderLeft(BorderStyle.MEDIUM, mergedCell, xlsxSheet);
            RegionUtil.setBorderRight(BorderStyle.MEDIUM, mergedCell, xlsxSheet);
        }
    }

    private void templateTimeSlots() {
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        for (int i = 0; i < DayOfWeek.values().length; i++) {
            for (TimeSlot timeSlot : timeSlots) {
                int rowNum = i * timeSlots.size() + timeSlot.getNumber() + TOP_OFFSET;
                Row row = xlsxSheet.getRow(rowNum);
                if (row == null) {
                    row = xlsxSheet.createRow(rowNum);
                }
                Cell cell = row.createCell(1);
                cell.setCellValue(timeSlot.toString());
                setStyleToCell(cell);
            }
        }
    }

    private void templateGroups() {
        List<Group> groups = groupRepository.findAll();
        Row row = xlsxSheet.createRow(TOP_OFFSET);
        for (int i = 0; i < groups.size(); i++) {
            Cell cell = row.createCell(i + 2);
            cell.setCellValue(groups.get(i).getNumber());
            setStyleToCell(cell);
        }
    }

    private void setStyleToCell(Cell cell) {
        CellStyle cellStyle = xlsxSheet.getWorkbook().createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cell.setCellStyle(cellStyle);
    }

    private void fillSchedule(Table<ScheduleCell, Group, Class> schedule) {
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        for (ScheduleCell scheduleCell : schedule.rowKeySet()) {
            int DayOfWeekNum = scheduleCell.getDayOfWeek().ordinal();
            int timeSlotNum = scheduleCell.getTimeSlot().getNumber();
            int rowNum = TOP_OFFSET + DayOfWeekNum * timeSlots.size() + timeSlotNum;
            List<Group> groups = new ArrayList<>(schedule.row(scheduleCell).keySet());

            for (Group group : groups) {

                //find group column
                Row rowWithGroupNums = xlsxSheet.getRow(TOP_OFFSET);
                Cell cellWithGroupNum;
                Iterator rowIterator = rowWithGroupNums.cellIterator();
                do {
                    cellWithGroupNum = (Cell) rowIterator.next();
                    if (cellWithGroupNum.getStringCellValue().equals(group.getNumber())) {
                        break;
                    }
                } while (rowIterator.hasNext());
                int colNum = cellWithGroupNum.getColumnIndex();

                Row row = xlsxSheet.getRow(rowNum);
                Class c = schedule.get(scheduleCell, group);
                Cell cell = row.createCell(colNum);
                cell.setCellValue(c.toString());
                CellStyle cs = xlsxSheet.getWorkbook().createCellStyle();
                cs.setWrapText(true);
                cell.setCellStyle(cs);
                row.setHeightInPoints((4 * xlsxSheet.getDefaultRowHeightInPoints()));
            }
        }
    }

    private void autoSize() {
        int numOfColumns = xlsxSheet.getRow(TOP_OFFSET).getLastCellNum();
        for (int i = 0; i <= numOfColumns; i++) {
            xlsxSheet.autoSizeColumn(i, true);
        }
    }

    private void saveToFile() {
        try {
            File file = new File(outputPath);
            file.createNewFile();
            xlsxSheet.getWorkbook().write(new FileOutputStream(file));
            xlsxSheet.getWorkbook().close();
        } catch (IOException e) {
            log.error("Could not create new file on path " + outputPath);
            e.printStackTrace();
        }
    }

}
