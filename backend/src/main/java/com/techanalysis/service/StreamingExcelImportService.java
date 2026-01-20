package com.techanalysis.service;

import com.techanalysis.entity.Paper;
import com.techanalysis.entity.Patent;
import com.techanalysis.entity.Project;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流式 Excel 导入服务，支持大文件处理
 * 使用 SAX 模式读取，内存占用低
 */
@Service
public class StreamingExcelImportService {

    private static final Logger log = LoggerFactory.getLogger(StreamingExcelImportService.class);
    private static final int BATCH_SIZE = 1000;

    private final PatentService patentService;
    private final ProjectService projectService;
    private final PaperService paperService;
    private final AnalysisService analysisService;

    public StreamingExcelImportService(PatentService patentService, ProjectService projectService, PaperService paperService, AnalysisService analysisService) {
        this.patentService = patentService;
        this.projectService = projectService;
        this.paperService = paperService;
        this.analysisService = analysisService;
    }

    @Transactional
    public int importPatentStreaming(MultipartFile file) throws Exception {
        log.info("开始流式导入专利数据，文件大小: {} MB", file.getSize() / 1024 / 1024);
        AtomicInteger totalCount = new AtomicInteger(0);
        List<Patent> batch = new ArrayList<>(BATCH_SIZE);

        try (InputStream is = file.getInputStream();
             OPCPackage pkg = OPCPackage.open(is)) {
            
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader reader = new XSSFReader(pkg);
            StylesTable styles = reader.getStylesTable();
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) reader.getSheetsData();

            if (sheets.hasNext()) {
                InputStream sheetStream = sheets.next();
                InputSource sheetSource = new InputSource(sheetStream);
                
                XMLReader parser = XMLReaderFactory.createXMLReader();
                PatentSheetHandler handler = new PatentSheetHandler(batch, totalCount, patentService);
                XSSFSheetXMLHandler sheetHandler = new XSSFSheetXMLHandler(styles, strings, handler, new DataFormatter(), false);
                parser.setContentHandler(sheetHandler);
                parser.parse(sheetSource);
                
                // 处理剩余数据
                if (!batch.isEmpty()) {
                    saveBatchPatents(batch);
                    totalCount.addAndGet(batch.size());
                    batch.clear();
                }
                
                sheetStream.close();
            }
        }

        log.info("专利流式导入完成，共 {} 条", totalCount.get());
        analysisService.clearAllCache(); // 清除缓存
        return totalCount.get();
    }

    @Transactional
    public int importProjectStreaming(MultipartFile file) throws Exception {
        log.info("开始流式导入项目数据，文件大小: {} MB", file.getSize() / 1024 / 1024);
        AtomicInteger totalCount = new AtomicInteger(0);
        List<Project> batch = new ArrayList<>(BATCH_SIZE);

        try (InputStream is = file.getInputStream();
             OPCPackage pkg = OPCPackage.open(is)) {
            
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader reader = new XSSFReader(pkg);
            StylesTable styles = reader.getStylesTable();
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) reader.getSheetsData();

            if (sheets.hasNext()) {
                InputStream sheetStream = sheets.next();
                InputSource sheetSource = new InputSource(sheetStream);
                
                XMLReader parser = XMLReaderFactory.createXMLReader();
                ProjectSheetHandler handler = new ProjectSheetHandler(batch, totalCount, projectService);
                XSSFSheetXMLHandler sheetHandler = new XSSFSheetXMLHandler(styles, strings, handler, new DataFormatter(), false);
                parser.setContentHandler(sheetHandler);
                parser.parse(sheetSource);
                
                if (!batch.isEmpty()) {
                    saveBatchProjects(batch);
                    totalCount.addAndGet(batch.size());
                    batch.clear();
                }
                
                sheetStream.close();
            }
        }

        log.info("项目流式导入完成，共 {} 条", totalCount.get());
        analysisService.clearAllCache(); // 清除缓存
        return totalCount.get();
    }

    @Transactional
    public int importPaperStreaming(MultipartFile file) throws Exception {
        log.info("开始流式导入论文数据，文件大小: {} MB", file.getSize() / 1024 / 1024);
        AtomicInteger totalCount = new AtomicInteger(0);
        List<Paper> batch = new ArrayList<>(BATCH_SIZE);

        try (InputStream is = file.getInputStream();
             OPCPackage pkg = OPCPackage.open(is)) {
            
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader reader = new XSSFReader(pkg);
            StylesTable styles = reader.getStylesTable();
            XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) reader.getSheetsData();

            if (sheets.hasNext()) {
                InputStream sheetStream = sheets.next();
                InputSource sheetSource = new InputSource(sheetStream);
                
                XMLReader parser = XMLReaderFactory.createXMLReader();
                PaperSheetHandler handler = new PaperSheetHandler(batch, totalCount, paperService);
                XSSFSheetXMLHandler sheetHandler = new XSSFSheetXMLHandler(styles, strings, handler, new DataFormatter(), false);
                parser.setContentHandler(sheetHandler);
                parser.parse(sheetSource);
                
                if (!batch.isEmpty()) {
                    saveBatchPapers(batch);
                    totalCount.addAndGet(batch.size());
                    batch.clear();
                }
                
                sheetStream.close();
            }
        }

        log.info("论文流式导入完成，共 {} 条", totalCount.get());
        analysisService.clearAllCache(); // 清除缓存
        return totalCount.get();
    }

    private void saveBatchPatents(List<Patent> batch) {
        for (Patent patent : batch) {
            try {
                patentService.save(patent);
            } catch (Exception e) {
                log.warn("保存专利失败: {}", e.getMessage());
            }
        }
    }

    private void saveBatchProjects(List<Project> batch) {
        for (Project project : batch) {
            try {
                projectService.save(project);
            } catch (Exception e) {
                log.warn("保存项目失败: {}", e.getMessage());
            }
        }
    }

    private void saveBatchPapers(List<Paper> batch) {
        for (Paper paper : batch) {
            try {
                paperService.save(paper);
            } catch (Exception e) {
                log.warn("保存论文失败: {}", e.getMessage());
            }
        }
    }

    // 专利数据行处理器
    // Excel列顺序: 序号 | 标题 | 摘要 | 申请人 | 公开号 | 公开年份 | 月份 | 公开国别 | IPC | 专利有效性 | 标题关键词 | 摘要关键词 | 标题实体 | 摘要实体
    private class PatentSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final List<Patent> batch;
        private final AtomicInteger totalCount;
        private final PatentService service;
        private Patent currentPatent;
        private int currentRow = -1;
        private int currentCol = -1;

        public PatentSheetHandler(List<Patent> batch, AtomicInteger totalCount, PatentService service) {
            this.batch = batch;
            this.totalCount = totalCount;
            this.service = service;
        }

        @Override
        public void startRow(int rowNum) {
            currentRow = rowNum;
            if (rowNum > 0) {
                currentPatent = new Patent();
            }
        }

        @Override
        public void endRow(int rowNum) {
            if (rowNum > 0 && currentPatent != null) {
                batch.add(currentPatent);
                if (batch.size() >= BATCH_SIZE) {
                    saveBatchPatents(batch);
                    totalCount.addAndGet(batch.size());
                    log.info("专利导入进度: {}", totalCount.get());
                    batch.clear();
                }
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, org.apache.poi.xssf.usermodel.XSSFComment comment) {
            if (currentRow == 0 || currentPatent == null) return;
            
            int col = getColIndex(cellReference);
            String value = formattedValue != null ? formattedValue.trim() : null;
            
            switch (col) {
                case 0 -> {} // 序号，跳过
                case 1 -> currentPatent.setTitle(value);
                case 2 -> currentPatent.setAbstractText(value);
                case 3 -> currentPatent.setApplicant(value);
                case 4 -> currentPatent.setPatentNumber(value);
                case 5 -> currentPatent.setPublicationYear(parseInteger(value));
                case 6 -> currentPatent.setPublicationMonth(parseInteger(value));
                case 7 -> currentPatent.setCountry(value);
                case 8 -> currentPatent.setClassificationCode(value);
                case 9 -> currentPatent.setValidity(value);
                case 10 -> currentPatent.setTitleKeywords(value);
                case 11 -> currentPatent.setAbstractKeywords(value);
                case 12 -> currentPatent.setTitleEntities(value);
                case 13 -> currentPatent.setAbstractEntities(value);
            }
        }
    }

    // 项目数据行处理器
    // Excel列顺序: 项目id | 标题 | 资助机构 | 承担机构 | 项目类型 | 项目金额 | 项目年份 | 项目月份 | 摘要 | 标题关键词 | 摘要关键词 | 标题实体 | 摘要实体
    private class ProjectSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final List<Project> batch;
        private final AtomicInteger totalCount;
        private final ProjectService service;
        private Project currentProject;
        private int currentRow = -1;

        public ProjectSheetHandler(List<Project> batch, AtomicInteger totalCount, ProjectService service) {
            this.batch = batch;
            this.totalCount = totalCount;
            this.service = service;
        }

        @Override
        public void startRow(int rowNum) {
            currentRow = rowNum;
            if (rowNum > 0) {
                currentProject = new Project();
            }
        }

        @Override
        public void endRow(int rowNum) {
            if (rowNum > 0 && currentProject != null) {
                batch.add(currentProject);
                if (batch.size() >= BATCH_SIZE) {
                    saveBatchProjects(batch);
                    totalCount.addAndGet(batch.size());
                    log.info("项目导入进度: {}", totalCount.get());
                    batch.clear();
                }
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, org.apache.poi.xssf.usermodel.XSSFComment comment) {
            if (currentRow == 0 || currentProject == null) return;
            
            int col = getColIndex(cellReference);
            String value = formattedValue != null ? formattedValue.trim() : null;
            
            switch (col) {
                case 0 -> currentProject.setProjectId(value);
                case 1 -> currentProject.setProjectName(value);
                case 2 -> currentProject.setFundingOrg(value);
                case 3 -> currentProject.setInstitution(value);
                case 4 -> currentProject.setProjectType(value);
                case 5 -> currentProject.setFundingAmount(parseDecimal(value));
                case 6 -> currentProject.setProjectYear(parseInteger(value));
                case 7 -> currentProject.setProjectMonth(parseInteger(value));
                case 8 -> currentProject.setAbstractText(value);
                case 9 -> currentProject.setTitleKeywords(value);
                case 10 -> currentProject.setAbstractKeywords(value);
                case 11 -> currentProject.setTitleEntities(value);
                case 12 -> currentProject.setAbstractEntities(value);
            }
        }
    }

    // 论文数据行处理器
    // Excel列顺序: Authors | Author Full Names | 标题 | Source Title | Document Type | Author Keywords | 摘要 | Addresses | ... | Countries | 标题关键词 | 摘要关键词 | 标题实体 | 摘要实体
    // 根据图片，列顺序为: 0-Authors, 1-Author Full Names, 2-标题, 3-Source Title, 4-Document Type, 5-Author Keywords, 6-摘要, 7-Addresses, ...
    // 需要找到 Publication Year 和 Countries 的位置
    private class PaperSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final List<Paper> batch;
        private final AtomicInteger totalCount;
        private final PaperService service;
        private Paper currentPaper;
        private int currentRow = -1;

        public PaperSheetHandler(List<Paper> batch, AtomicInteger totalCount, PaperService service) {
            this.batch = batch;
            this.totalCount = totalCount;
            this.service = service;
        }

        @Override
        public void startRow(int rowNum) {
            currentRow = rowNum;
            if (rowNum > 0) {
                currentPaper = new Paper();
            }
        }

        @Override
        public void endRow(int rowNum) {
            if (rowNum > 0 && currentPaper != null) {
                batch.add(currentPaper);
                if (batch.size() >= BATCH_SIZE) {
                    saveBatchPapers(batch);
                    totalCount.addAndGet(batch.size());
                    log.info("论文导入进度: {}", totalCount.get());
                    batch.clear();
                }
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, org.apache.poi.xssf.usermodel.XSSFComment comment) {
            if (currentRow == 0 || currentPaper == null) return;
            
            int col = getColIndex(cellReference);
            String value = formattedValue != null ? formattedValue.trim() : null;
            
            // 根据你的Excel列顺序调整
            // Authors | Author Full Names | 标题 | Source Title | Document Type | Author Keywords | 摘要 | Addresses | Researcher Ids | ORCIDs | Funding Orgs | Funding Name Preferred | Cited Reference Count | Times Cited, WoS Core | Times Cited, All Databases | 180 Day Usage Count | Since 2013 Usage Count | Journal Abbreviation | Journal ISO Abbreviation | Publication Date | Publication Year | DOI | DOI Link | WoS Categories | Research Areas | Countries | 标题关键词 | 摘要关键词 | 标题实体 | 摘要实体
            switch (col) {
                case 0 -> currentPaper.setAuthors(value);
                case 1 -> currentPaper.setAuthorFullNames(value);
                case 2 -> currentPaper.setTitle(value);
                case 3 -> { currentPaper.setSourceTitle(value); currentPaper.setJournal(value); }
                case 4 -> currentPaper.setDocumentType(value);
                case 5 -> currentPaper.setAuthorKeywords(value);
                case 6 -> currentPaper.setAbstractText(value);
                case 7 -> currentPaper.setAddresses(value);
                // 8-9: Researcher Ids, ORCIDs (跳过)
                // 10-11: Funding Orgs, Funding Name Preferred (跳过)
                // 12: Cited Reference Count (跳过)
                case 13 -> currentPaper.setCitationCount(parseInteger(value)); // Times Cited, WoS Core
                // 14-17: Times Cited All, Usage counts (跳过)
                // 18-19: Journal Abbreviations (跳过)
                // 20: Publication Date (跳过)
                case 20 -> currentPaper.setPublicationYear(parseInteger(value)); // Publication Year
                case 21 -> currentPaper.setDoi(value); // DOI
                // 22: DOI Link (跳过)
                case 23 -> currentPaper.setWosCategories(value); // WoS Categories
                case 24 -> currentPaper.setResearchAreas(value); // Research Areas
                case 25 -> currentPaper.setCountry(value); // Countries
                case 26 -> currentPaper.setTitleKeywords(value); // 标题关键词
                case 27 -> currentPaper.setAbstractKeywords(value); // 摘要关键词
                case 28 -> currentPaper.setTitleEntities(value); // 标题实体
                case 29 -> currentPaper.setAbstractEntities(value); // 摘要实体
            }
        }
    }

    private static int getColIndex(String cellReference) {
        StringBuilder sb = new StringBuilder();
        for (char c : cellReference.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(c);
            } else {
                break;
            }
        }
        String col = sb.toString().toUpperCase();
        int result = 0;
        for (int i = 0; i < col.length(); i++) {
            result = result * 26 + (col.charAt(i) - 'A' + 1);
        }
        return result - 1;
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            // 尝试多种日期格式
            String[] patterns = {"yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd", "yyyy年MM月dd日"};
            for (String pattern : patterns) {
                try {
                    return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
                } catch (DateTimeParseException ignored) {}
            }
            // 尝试解析 Excel 数字日期
            if (value.matches("\\d+(\\.\\d+)?")) {
                double excelDate = Double.parseDouble(value);
                return LocalDate.of(1899, 12, 30).plusDays((long) excelDate);
            }
        } catch (Exception e) {
            // 忽略解析错误
        }
        return null;
    }

    private static BigDecimal parseDecimal(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return new BigDecimal(value.replaceAll("[^\\d.-]", ""));
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return (int) Double.parseDouble(value.replaceAll("[^\\d.-]", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
